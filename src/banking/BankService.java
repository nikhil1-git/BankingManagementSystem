
package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankService {

    public BankAccount createAccount(String name, double openingBalance) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Invalid opening balance.");
        }

        String sql = "INSERT INTO accounts (customer_name, balance) VALUES (?, ?)";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int accountNumber;

                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, name.trim());
                    ps.setDouble(2, openingBalance);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Account number was not generated.");
                        }
                        accountNumber = keys.getInt(1);
                    }
                }

                saveTransaction(con, accountNumber, "ACCOUNT_OPENED",
                        openingBalance);

                con.commit();

                return new BankAccount(
                        accountNumber, name.trim(), openingBalance);

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not create account.", e);
        }
    }

    public BankAccount findAccount(int accountNumber) {

        String sql = "SELECT account_number, customer_name, balance "
                + "FROM accounts WHERE account_number = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BankAccount(
                            rs.getInt("account_number"),
                            rs.getString("customer_name"),
                            rs.getDouble("balance")
                    );
                }
            }

            return null;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not find account.", e);
        }
    }

    public boolean deposit(int accountNumber, double amount) {

        if (!Double.isFinite(amount) || amount <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance + ? "
                + "WHERE account_number = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int updated;

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setDouble(1, amount);
                    ps.setInt(2, accountNumber);
                    updated = ps.executeUpdate();
                }

                if (updated == 0) {
                    con.rollback();
                    return false;
                }

                saveTransaction(con, accountNumber, "DEPOSIT", amount);
                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not deposit money.", e);
        }
    }

    public boolean withdraw(int accountNumber, double amount) {

        if (!Double.isFinite(amount) || amount <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance - ? "
                + "WHERE account_number = ? AND balance >= ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int updated;

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setDouble(1, amount);
                    ps.setInt(2, accountNumber);
                    ps.setDouble(3, amount);
                    updated = ps.executeUpdate();
                }

                if (updated == 0) {
                    con.rollback();
                    return false;
                }

                saveTransaction(con, accountNumber, "WITHDRAWAL", amount);
                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not withdraw money.", e);
        }
    }

    public List<BankAccount> getAllAccounts() {

        List<BankAccount> accounts = new ArrayList<>();

        String sql = "SELECT account_number, customer_name, balance FROM accounts";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                accounts.add(new BankAccount(
                        rs.getInt("account_number"),
                        rs.getString("customer_name"),
                        rs.getDouble("balance")
                ));
            }

            return accounts;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load accounts.", e);
        }
    }

    public List<String> getTransactions(int accountNumber) {

        List<String> transactions = new ArrayList<>();

        String sql = "SELECT transaction_type, amount, transaction_date "
                + "FROM transactions WHERE account_number = ? "
                + "ORDER BY transaction_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("transaction_type");
                    double amount = rs.getDouble("amount");
                    String date = rs.getTimestamp("transaction_date").toString();

                    String description;

                    switch (type) {
                        case "ACCOUNT_OPENED":
                            description = "Account opened with Rs. " + amount;
                            break;
                        case "DEPOSIT":
                            description = "Deposited Rs. " + amount;
                            break;
                        case "WITHDRAWAL":
                            description = "Withdrawn Rs. " + amount;
                            break;
                        default:
                            description = type + " Rs. " + amount;
                    }

                    transactions.add(description + " | " + date);
                }
            }

            return transactions;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load transactions.", e);
        }
    }

    private void saveTransaction(Connection con, int accountNumber,
                                 String type, double amount)
            throws SQLException {

        String sql = "INSERT INTO transactions "
                + "(account_number, transaction_type, amount) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }
}
