package atm.state;

import atm.machine.ATMMachine;

/**
 * State allowing the authenticated customer to select a banking transaction (Withdraw or Deposit)
 * or exit the session.
 */
public class TransactionSelectionState extends ATMState {

    @Override
    public void processWithdrawalRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Withdrawal selected. Please enter the amount to withdraw.");
        atmMachine.transitionToState(new WithdrawAmountEntryState());
    }

    @Override
    public void processDepositRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Deposit selected. Please deposit funds.");
        atmMachine.transitionToState(new DepositCollectionState());
    }

    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Thank you for banking with us. Card ejected.");
        atmMachine.getCardProcessor().ejectCard();
        atmMachine.transitionToState(new IdleState());
    }
}
