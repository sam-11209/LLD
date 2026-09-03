package atm.state;

import atm.account.Account;
import atm.machine.ATMMachine;
import atm.transaction.DepositTransaction;
import java.math.BigDecimal;

/**
 * State capturing and executing cash deposit transactions.
 */
public class DepositCollectionState extends ATMState {

    @Override
    public void processDepositCollection(ATMMachine atmMachine, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            atmMachine.getDisplay().showMessage("Invalid deposit amount. Must be strictly positive.");
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

        // Hardware collects cash
        atmMachine.getDepositBox().collectDeposit(amount);

        // Execute transaction via polymorphism (Strategy-like pattern)
        DepositTransaction depositTx = new DepositTransaction(account, amount);
        depositTx.executeTransaction();

        atmMachine.getDisplay().showMessage(
                "Deposit successful of $" + amount + ". New balance: $" + account.getBalance());

        // Return to TransactionSelectionState for next transaction or exit
        atmMachine.transitionToState(new TransactionSelectionState());
    }

    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Deposit cancelled. Card ejected.");
        atmMachine.getCardProcessor().ejectCard();
        atmMachine.transitionToState(new IdleState());
    }
}
