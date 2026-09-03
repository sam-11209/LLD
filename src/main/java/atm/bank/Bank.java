package atm.bank;

import atm.account.Account;
import atm.account.AccountType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of {@link BankInterface}.
 *
 * <p><b>Concurrency & Thread Safety Strategy:</b>
 * <ul>
 *   <li>The internal account directories ({@code accounts} and {@code accountByCard}) use
 *       {@link ConcurrentHashMap} to support safe, concurrent lookups, additions, and updates
 *       from multiple ATM machine threads simultaneously.</li>
 *   <li>{@link #withdrawFunds(Account, BigDecimal)} is the critical financial check-then-act section.
 *       It acquires the target account's fine-grained lock ({@code account.getLock()}) to ensure the
 *       sufficiency check and debit operation execute atomically, preventing double-withdrawal race
 *       conditions.</li>
 *   <li>Locking is deliberately scoped to the individual {@link Account} rather than using a synchronized
 *       method on {@code Bank}. This avoids serializing transactions across distinct accounts, preserving
 *       high system throughput.</li>
 * </ul>
 */
public class Bank implements BankInterface {

    /**
     * Lookup table mapping account number to Account instance.
     * ConcurrentHashMap guarantees thread safety under simultaneous multi-ATM access.
     */
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    /**
     * Lookup table mapping debit card number to Account instance.
     * ConcurrentHashMap guarantees thread safety under simultaneous multi-ATM access.
     */
    private final Map<String, Account> accountByCard = new ConcurrentHashMap<>();

    @Override
    public void addAccount(String accountNumber, AccountType type, String cardNumber, String pin) {
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(type, "accountType cannot be null");
        Objects.requireNonNull(cardNumber, "cardNumber cannot be null");
        Objects.requireNonNull(pin, "pin cannot be null");

        Account account = new Account(accountNumber, type, cardNumber, pin);
        accounts.put(accountNumber, account);
        accountByCard.put(cardNumber, account);
    }

    @Override
    public boolean validateCard(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        return accountByCard.containsKey(cardNumber);
    }

    @Override
    public boolean checkPin(String cardNumber, String pinNumber) {
        Account account = getAccountByCard(cardNumber);
        if (account == null) {
            return false;
        }
        return account.validatePin(pinNumber);
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accounts.get(accountNumber);
    }

    @Override
    public Account getAccountByCard(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        return accountByCard.get(cardNumber);
    }

    @Override
    public boolean withdrawFunds(Account account, BigDecimal amount) {
        Objects.requireNonNull(account, "account cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        /*
         * Critical Section:
         * We lock the specific account's ReentrantLock for the entire check-then-act sequence.
         * This prevents two concurrent threads (e.g. two ATMs accessing the same account)
         * from both validating sufficient balance before either debits it (race condition / double-spend).
         */
        account.getLock().lock();
        try {
            if (account.getBalance().compareTo(amount) >= 0) {
                account.updateBalanceWithTransaction(amount.negate());
                return true;
            }
            return false;
        } finally {
            account.getLock().unlock();
        }
    }
}
