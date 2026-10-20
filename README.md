# Banking Management System

A beginner-friendly Java console application for managing bank accounts using Java, JDBC, and MySQL.

## Features

- Create a bank account
- Check an account balance
- Deposit money
- Withdraw money while preventing overdrafts
- View transaction history
- View all accounts
- Store account and transaction data in MySQL

## Technologies Used

- Java (Java 14 or later recommended because the menu uses arrow-style `switch` cases)
- JDBC
- MySQL
- IntelliJ IDEA (recommended IDE)

## Project Structure

```text
JavaTutorial/
└── src/
    └── banking/
        ├── BankAccount.java
        ├── BankService.java
        ├── BankingManagementSystem.java
        └── DatabaseConnection.java
```

## Database Setup

1. Install and start MySQL Server.
2. Create the database:

```sql
CREATE DATABASE banking_db;
USE banking_db;

CREATE TABLE accounts (
    account_number INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number INT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
```

## Configure MySQL Connection

Open `DatabaseConnection.java` and update the connection settings to match your local MySQL setup:

- URL: `jdbc:mysql://localhost:3306/banking_db`
- USER: your MySQL username
- PASSWORD: your MySQL password

Do not commit real database passwords to a public GitHub repository. For a learning project, use a local configuration and keep credentials out of Git.

## Add the MySQL JDBC Driver

Add the MySQL Connector/J dependency to the project. If you use Maven, add the MySQL Connector/J dependency to your `pom.xml`. If you are not using Maven, add the Connector/J JAR to the project libraries.

## Run the Application

1. Open the project in IntelliJ IDEA.
2. Make sure MySQL is running and the database and tables are created.
3. Configure the database credentials.
4. Run `DatabaseConnection.java` first to test the connection.
5. Run `BankingManagementSystem.java`.
6. Follow the console menu.

## Menu Options

1. Create Account
2. Check Balance
3. Deposit Money
4. Withdraw Money
5. Transaction History
6. View All Accounts
7. Exit

## Important Notes

- This is an educational project, not a production banking system.
- Use `BigDecimal` rather than `double` for money in a real financial application.
- Before uploading publicly, remove or externalize database credentials.
- The current console transaction-history method should be connected to `BankService.getTransactions(accountNumber)` so it reads transaction records from MySQL.
- After a deposit or withdrawal, reload the account from the database before displaying its updated balance; the `BankAccount` object initially loaded by the console can otherwise show an old balance.

## Author

Created as a Java and MySQL learning project.
