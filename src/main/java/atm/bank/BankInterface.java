package atm.bank;

import atm.account.Account;
import atm.account.AccountType;
import java.math.BigDecimal;

/**
 * Interface representing banking backend operations.
 * Allows decoupling the ATM machine from the in-memory or networked bank implementation
 * (Dependency Inversion Principle).
 */
public interface BankInterface {

    /**
     * Registers a new account with the bank.
     */
    void addAccount(String accountNumber, AccountType type, String cardNumber, String pin);

    /**
     * Checks if a card number is recognized and registered with an account.
     */
    boolean validateCard(String cardNumber);

    /**
     * Validates that the provided PIN matches the account linked to the given card number.
     */
    boolean checkPin(String cardNumber, String pinNumber);

    /**
     * Retrieves an account by its unique account number.
     */
    Account getAccountByAccountNumber(String accountNumber);

    /**
     * Retrieves an account associated with a specific debit card number.
     */
    Account getAccountByCard(String cardNumber);

    /**
     * Executes an atomic check-then-act withdrawal against the provided account.
     *
     * @param account the target account
     * @param amount  the amount to withdraw
     * @return {@code true} if sufficient funds were available and debited; {@code false} otherwise
     */
    boolean withdrawFunds(Account account, BigDecimal amount);
}
