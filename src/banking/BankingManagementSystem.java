
package banking;

import java.util.List;
import java.util.Scanner;

public class BankingManagementSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final BankService bank = new BankService();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Transaction History");
            System.out.println("6. View All Accounts");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> checkBalance();
                case 3 -> depositMoney();
                case 4 -> withdrawMoney();
                case 5 -> viewTransactions();
                case 6 -> viewAllAccounts();
                case 7 -> System.out.println("Thank you!");
                default -> System.out.println("Invalid choice!");
            }

        } while (choice != 7);

        sc.close();
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid whole number: ");
            }
        }
    }

    private static double readAmount() {
        while (true) {
            try {
                double amount = Double.parseDouble(sc.nextLine().trim());

                if (Double.isFinite(amount) && amount > 0) {
                    return amount;
                }

                System.out.print("Enter an amount greater than zero: ");
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid amount: ");
            }
        }
    }

    private static BankAccount askForAccount() {
        System.out.print("Enter account number: ");
        int number = readInt();

        BankAccount account = bank.findAccount(number);

        if (account == null) {
            System.out.println("Account not found!");
        }

        return account;
    }

    private static void createAccount() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        System.out.print("Enter opening balance: ");
        double amount = readAmount();

        try {
            BankAccount account = bank.createAccount(name, amount);
            System.out.println("Account created successfully!");
            System.out.println("Account number: " + account.getAccountNumber());
            System.out.println("Customer: " + account.getCustomerName());
            System.out.printf("Balance: Rs. %.2f%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkBalance() {
        BankAccount account = askForAccount();

        if (account != null) {
            System.out.printf("Current balance: Rs. %.2f%n",
                    account.getBalance());
        }
    }

    private static void depositMoney() {
        BankAccount account = askForAccount();

        if (account == null) {
            return;
        }

        System.out.print("Enter deposit amount: ");
        double amount = readAmount();

        if (bank.deposit(account.getAccountNumber(), amount)) {
            System.out.println("Deposit successful!");
            checkBalanceFor(account);
        } else {
            System.out.println("Deposit failed.");
        }
    }

    private static void withdrawMoney() {
        BankAccount account = askForAccount();

        if (account == null) {
            return;
        }

        System.out.print("Enter withdrawal amount: ");
        double amount = readAmount();

        if (bank.withdraw(account.getAccountNumber(), amount)) {
            System.out.println("Withdrawal successful!");
            checkBalanceFor(account);
        } else {
            System.out.println("Withdrawal failed. Check your balance.");
        }
    }

    private static void checkBalanceFor(BankAccount account) {
        System.out.printf("Current balance: Rs. %.2f%n",
                account.getBalance());
    }

    private static void viewTransactions() {
        BankAccount account = askForAccount();

        if (account == null) {
            return;
        }

        System.out.println("\n===== TRANSACTION HISTORY =====");

        for (String transaction : account.getTransactions()) {
            System.out.println(transaction);
        }
    }

    private static void viewAllAccounts() {
        List<BankAccount> accounts = bank.getAllAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (BankAccount account : accounts) {
            System.out.printf(
                    "Account: %d | Name: %s | Balance: Rs. %.2f%n",
                    account.getAccountNumber(),
                    account.getCustomerName(),
                    account.getBalance()
            );
        }
    }
}
