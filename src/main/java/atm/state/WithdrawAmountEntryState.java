package atm.state;

import atm.account.Account;
import atm.machine.ATMMachine;
import java.math.BigDecimal;

/**
 * State capturing and executing withdrawal requests.
 */
public class WithdrawAmountEntryState extends ATMState {

    @Override
    public void processAmountEntry(ATMMachine atmMachine, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            atmMachine.getDisplay().showMessage("Invalid amount entered. Amount must be positive.");
            atmMachine.transitionToState(new TransactionSelectionState());
            return;
        }

        String cardNumber = atmMachine.getCardProcessor().getCardNumber();
        Account account = atmMachine.getBankInterface().getAccountByCard(cardNumber);

        if (account == null) {
            atmMachine.getDisplay().showMessage("Account not found. Ejecting card.");
            atmMachine.getCardProcessor().ejectCard();
            atmMachine.transitionToState(new IdleState());
            return;
        }

        boolean success = atmMachine.getBankInterface().withdrawFunds(account, amount);
        if (success) {
            atmMachine.getCashDispenser().dispenseCash(amount);
            atmMachine.getDisplay().showMessage(
                    "Withdrawal successful of $" + amount + ". Remaining balance: $" + account.getBalance());
        } else {
            atmMachine.getDisplay().showMessage(
                    "Withdrawal failed: Insufficient funds or invalid amount.");
        }

        // Return to TransactionSelectionState to permit another transaction or card ejection
        atmMachine.transitionToState(new TransactionSelectionState());
    }

    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Transaction cancelled. Card ejected.");
        atmMachine.getCardProcessor().ejectCard();
        atmMachine.transitionToState(new IdleState());
    }
}
