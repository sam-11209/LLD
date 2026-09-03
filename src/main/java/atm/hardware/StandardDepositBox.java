package atm.hardware;

import java.math.BigDecimal;

/**
 * Standard implementation of {@link DepositBox}.
 */
public class StandardDepositBox implements DepositBox {

    @Override
    public void collectDeposit(BigDecimal amount) {
        System.out.println("[DEPOSIT BOX]: Received cash deposit of $" + amount);
    }
}
