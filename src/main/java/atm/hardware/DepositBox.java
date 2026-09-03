package atm.hardware;

import java.math.BigDecimal;

/**
 * Interface abstracting the deposit slot / cash acceptance box.
 */
public interface DepositBox {

    /**
     * Collects and validates physical cash or envelopes inserted by the user.
     *
     * @param amount cash amount collected
     */
    void collectDeposit(BigDecimal amount);
}
