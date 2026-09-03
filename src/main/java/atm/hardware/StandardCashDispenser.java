package atm.hardware;

import java.math.BigDecimal;

/**
 * Standard implementation of {@link CashDispenser}.
 */
public class StandardCashDispenser implements CashDispenser {

    @Override
    public void dispenseCash(BigDecimal amount) {
        System.out.println("[CASH DISPENSER]: Dispensed $" + amount);
    }
}
