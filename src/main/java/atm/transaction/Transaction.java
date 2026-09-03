package atm.transaction;

/**
 * Strategy-like abstraction for an executable financial operation.
 * Adheres to the Open/Closed Principle: new transaction types (e.g. Transfer)
 * can be added without modifying existing transaction processing logic.
 */
public interface Transaction {

    /**
     * Returns the type of transaction.
     */
    TransactionType getType();

    /**
     * Pre-validates whether this transaction can be safely executed
     * (e.g. checks fund sufficiency or deposit constraints).
     *
     * @return {@code true} if valid; {@code false} otherwise
     */
    boolean validateTransaction();

    /**
     * Executes the financial operation against the underlying account.
     */
    void executeTransaction();
}
