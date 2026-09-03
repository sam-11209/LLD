package atm.state;

import atm.machine.ATMMachine;

/**
 * State managing user PIN verification with a limit of 3 failed attempts before retaining the card.
 */
public class PinEntryState extends ATMState {

    private static final int MAX_PIN_ATTEMPTS = 3;
    private int failedAttempts = 0;

    @Override
    public void processPinEntry(ATMMachine atmMachine, String pin) {
        String cardNumber = atmMachine.getCardProcessor().getCardNumber();
        boolean pinValid = atmMachine.getBankInterface().checkPin(cardNumber, pin);

        if (pinValid) {
            atmMachine.getDisplay().showMessage("PIN verified successfully. Please select a transaction.");
            atmMachine.transitionToState(new TransactionSelectionState());
        } else {
            failedAttempts++;
            if (failedAttempts >= MAX_PIN_ATTEMPTS) {
                atmMachine.getDisplay().showMessage("Maximum PIN attempts exceeded. Card retained for security.");
                atmMachine.getCardProcessor().retainCard();
                atmMachine.transitionToState(new IdleState());
            } else {
                int remaining = MAX_PIN_ATTEMPTS - failedAttempts;
                atmMachine.getDisplay().showMessage("Invalid PIN. Attempts remaining: " + remaining);
            }
        }
    }

    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Session cancelled. Card ejected.");
        atmMachine.getCardProcessor().ejectCard();
        atmMachine.transitionToState(new IdleState());
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }
}
