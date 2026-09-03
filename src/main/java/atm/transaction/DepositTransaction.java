package atm.transaction;

import atm.account.Account;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Encapsulates a cash/fund deposit operation into a bank account.
 */
public class DepositTransaction implements Transaction {

    private final Account account;
    private final BigDecimal amount;

    /**
     * Constructs a DepositTransaction.
     *
     * @param account target account to credit
     * @param amount  monetary amount to deposit (must be positive)
     */
    public DepositTransaction(Account account, BigDecimal amount) {
        this.account = Objects.requireNonNull(account, "account cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly positive");
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.DEPOSIT;
    }

    @Override
    public boolean validateTransaction() {
        return true;
    }

    @Override
    public void executeTransaction() {
        account.updateBalanceWithTransaction(amount);
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
