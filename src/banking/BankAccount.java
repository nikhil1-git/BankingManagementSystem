
package banking;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    private final int accountNumber;
    private final String customerName;
    private double balance;
    private final List<String> transactions = new ArrayList<>();

    public BankAccount(int accountNumber, String customerName,
                       double openingBalance) {

        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = openingBalance;

        transactions.add(
                "Account opened with Rs. " + openingBalance
        );
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add("Deposited Rs. " + amount);
    }

    public boolean withdraw(double amount) {

        if (amount > balance) {
            return false;
        }

        balance -= amount;
        transactions.add("Withdrawn Rs. " + amount);
        return true;
    }
}
