package atm.hardware;

import java.math.BigDecimal;

/**
 * Interface abstracting the physical Cash Dispenser mechanism.
 */
public interface CashDispenser {

    /**
     * Dispenses the requested monetary amount in physical cash.
     *
     * @param amount cash amount to dispense
     */
    void dispenseCash(BigDecimal amount);
}
