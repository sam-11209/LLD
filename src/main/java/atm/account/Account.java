package atm.account;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a customer bank account.
 *
 * <p><b>Concurrency Strategy & Locking Justification:</b>
 * <ul>
 *   <li>Identity fields ({@code accountNumber}, {@code cardNumber}, {@code cardPinHash}, {@code accountType})
 *       are marked {@code final} and effectively immutable; they require no synchronization after construction.</li>
 *   <li>{@code balance} is mutable and represents the critical state. An instance-level {@link ReentrantLock}
 *       guards balance reads and mutations, as well as compound check-then-act operations (e.g. withdrawal
 *       balance checks) executed by {@code Bank}.</li>
 *   <li>Locking per-account rather than locking the entire bank ensures high concurrency across different
 *       accounts while strictly serializing concurrent operations on the same account.</li>
 * </ul>
 */
public class Account {

    private final String accountNumber;
    private final String cardNumber;
    private final byte[] cardPinHash;
    private final AccountType accountType;

    /**
     * Account-level lock used to guard balance mutations and compound check-then-act operations.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Mutable account balance. Must always be accessed or modified while holding {@link #lock}.
     */
    private BigDecimal balance;

    /**
     * Constructs a new Account with an initial balance of 0.
     *
     * @param accountNumber unique account identifier
     * @param accountType   type of account (CHECKING or SAVINGS)
     * @param cardNumber    associated debit card number
     * @param pin           plaintext PIN that is immediately hashed and never stored in plaintext
     */
    public Account(String accountNumber, AccountType accountType, String cardNumber, String pin) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        this.accountType = Objects.requireNonNull(accountType, "accountType cannot be null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "cardNumber cannot be null");
        Objects.requireNonNull(pin, "pin cannot be null");

        this.cardPinHash = hashPin(pin);
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Validates the provided plaintext PIN against the stored hash.
     * Uses {@link MessageDigest#isEqual(byte[], byte[])} to protect against timing attacks.
     *
     * @param pinNumber plaintext PIN to validate
     * @return {@code true} if matching, {@code false} otherwise
     */
    public boolean validatePin(String pinNumber) {
        if (pinNumber == null) {
            return false;
        }
        byte[] inputHash = hashPin(pinNumber);
        return MessageDigest.isEqual(this.cardPinHash, inputHash);
    }

    /**
     * Atomically modifies the balance by adding {@code balanceChange}.
     * Pass a positive value for deposits, or a negative value for withdrawals.
     *
     * @param balanceChange the change to apply (positive or negative)
     */
    public void updateBalanceWithTransaction(BigDecimal balanceChange) {
        Objects.requireNonNull(balanceChange, "balanceChange cannot be null");
        lock.lock();
        try {
            this.balance = this.balance.add(balanceChange);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the account-level lock to allow external atomic operations (e.g. check-then-act
     * fund withdrawal in {@code Bank}).
     *
     * @return the {@link ReentrantLock} guarding this account
     */
    public ReentrantLock getLock() {
        return lock;
    }

    /**
     * Returns the current balance under lock.
     *
     * @return current account balance
     */
    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public byte[] getCardPinHash() {
        return Arrays.copyOf(cardPinHash, cardPinHash.length);
    }

    public AccountType getAccountType() {
        return accountType;
    }

    private static byte[] hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(pin.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available in JVM environment", e);
        }
    }
}
