
package banking;

import java.util.ArrayList;
import java.util.List;

public class BankService {

    private final List<BankAccount> accounts = new ArrayList<>();
    private int nextAccountNumber = 1001;

    public BankAccount createAccount(String name, double openingBalance) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Invalid opening balance.");
        }

        BankAccount account = new BankAccount(
                nextAccountNumber++, name.trim(), openingBalance
        );

        accounts.add(account);
        return account;
    }

    public BankAccount findAccount(int accountNumber) {

        for (BankAccount account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }

        return null;
    }

    public boolean deposit(int accountNumber, double amount) {

        BankAccount account = findAccount(accountNumber);

        if (account == null || !Double.isFinite(amount) || amount <= 0) {
            return false;
        }

        account.deposit(amount);
        return true;
    }

    public boolean withdraw(int accountNumber, double amount) {

        BankAccount account = findAccount(accountNumber);

        if (account == null || !Double.isFinite(amount) || amount <= 0) {
            return false;
        }

        return account.withdraw(amount);
    }

    public List<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts);
    }
}
