package atm.state;

import atm.machine.ATMMachine;

/**
 * State representing an idle ATM machine awaiting a user to insert a debit card.
 */
public class IdleState extends ATMState {

    @Override
    public void processCardInsertion(ATMMachine atmMachine, String cardNumber) {
        if (atmMachine.getBankInterface().validateCard(cardNumber)) {
            atmMachine.getCardProcessor().insertCard(cardNumber);
            atmMachine.getDisplay().showMessage("Card accepted. Please enter your PIN.");
            atmMachine.transitionToState(new PinEntryState());
        } else {
            atmMachine.getDisplay().showMessage("Invalid card. Card rejected.");
            // Stays in IdleState
        }
    }
}
