package atm.transaction;

import atm.account.Account;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Encapsulates a cash withdrawal operation against a bank account.
 */
public class WithdrawTransaction implements Transaction {

    private final Account account;
    private final BigDecimal amount;

    /**
     * Constructs a WithdrawTransaction and pre-validates fund sufficiency.
     *
     * @param account target account to debit
     * @param amount  monetary amount to withdraw (must be positive)
     * @throws IllegalStateException if insufficient funds are available
     * @throws IllegalArgumentException if amount is non-positive or null
     */
    public WithdrawTransaction(Account account, BigDecimal amount) {
        this.account = Objects.requireNonNull(account, "account cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly positive");
        }

        if (!validateTransaction()) {
            throw new IllegalStateException("Insufficient funds for withdrawal");
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WITHDRAW;
    }

    @Override
    public boolean validateTransaction() {
        return account.getBalance().compareTo(amount) >= 0;
    }

    @Override
    public void executeTransaction() {
        /*
         * Acquire the account lock to ensure balance check and debit occur atomically,
         * protecting against concurrent withdrawals.
         */
        account.getLock().lock();
        try {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds at execution time");
            }
            account.updateBalanceWithTransaction(amount.negate());
        } finally {
            account.getLock().unlock();
        }
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
