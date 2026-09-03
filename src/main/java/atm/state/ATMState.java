package atm.state;

import atm.machine.ATMMachine;
import java.math.BigDecimal;

/**
 * Base class for the State pattern managing the ATM session lifecycle.
 *
 * <p><b>Design Pattern — State Pattern:</b>
 * Encapsulates state-specific behavior and transitions. Each user interaction
 * is delegated by {@link ATMMachine} to the current state object. By providing
 * default implementations that display "Invalid action, please try again.",
 * concrete states only override actions valid within their specific stage of the session.
 */
public abstract class ATMState {

    protected static final String INVALID_ACTION_MESSAGE = "Invalid action, please try again.";

    /**
     * Handles insertion of a debit card.
     */
    public void processCardInsertion(ATMMachine atmMachine, String cardNumber) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles card ejection or cancellation of the session.
     */
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles user PIN entry.
     */
    public void processPinEntry(ATMMachine atmMachine, String pin) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles user request to start a withdrawal transaction.
     */
    public void processWithdrawalRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles user request to start a deposit transaction.
     */
    public void processDepositRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles entry of a withdrawal amount.
     */
    public void processAmountEntry(ATMMachine atmMachine, BigDecimal amount) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }

    /**
     * Handles physical cash deposit collection.
     */
    public void processDepositCollection(ATMMachine atmMachine, BigDecimal amount) {
        atmMachine.getDisplay().showMessage(INVALID_ACTION_MESSAGE);
    }
}
