package atm.machine;

import atm.bank.BankInterface;
import atm.hardware.CardProcessor;
import atm.hardware.CashDispenser;
import atm.hardware.DepositBox;
import atm.hardware.Display;
import atm.hardware.Keypad;
import atm.state.ATMState;
import atm.state.IdleState;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Main coordinator and entry point for the ATM system.
 *
 * <p><b>Design Patterns Used:</b>
 * <ul>
 *   <li><b>Facade Pattern:</b> Simplifies client interaction by presenting a unified, high-level interface
 *       ({@code insertCard}, {@code enterPin}, {@code withdrawRequest}, {@code depositRequest}, etc.)
 *       over the underlying subsystems: hardware devices, state machine, and banking backend.</li>
 *   <li><b>State Pattern Context:</b> Maintains the current {@link ATMState} and delegates all session-related
 *       requests directly to that state object.</li>
 * </ul>
 *
 * <p><b>Concurrency Decision Note:</b>
 * The {@code state} field is intentionally <b>not</b> synchronized. An individual physical ATM machine
 * models a single physical console where only one user/session interacts with the machine at any given time.
 * All concurrent contention and multi-threaded race conditions (e.g. simultaneous operations against the same
 * bank account from multiple ATMs) are isolated to and safely resolved within the {@link BankInterface}
 * and {@link atm.account.Account} layers.
 */
public class ATMMachine {

    private final BankInterface bank;
    private final CardProcessor cardProcessor;
    private final DepositBox depositBox;
    private final CashDispenser cashDispenser;
    private final Keypad keypad;
    private final Display display;

    /**
     * Current state of the ATM session.
     */
    private ATMState state;

    /**
     * Constructs a new ATMMachine with all required dependencies injected via constructor.
     */
    public ATMMachine(
            BankInterface bank,
            CardProcessor cardProcessor,
            DepositBox depositBox,
            CashDispenser cashDispenser,
            Keypad keypad,
            Display display) {

        this.bank = Objects.requireNonNull(bank, "bank cannot be null");
        this.cardProcessor = Objects.requireNonNull(cardProcessor, "cardProcessor cannot be null");
        this.depositBox = Objects.requireNonNull(depositBox, "depositBox cannot be null");
        this.cashDispenser = Objects.requireNonNull(cashDispenser, "cashDispenser cannot be null");
        this.keypad = Objects.requireNonNull(keypad, "keypad cannot be null");
        this.display = Objects.requireNonNull(display, "display cannot be null");

        this.state = new IdleState();
    }

    public void insertCard(String cardNumber) {
        state.processCardInsertion(this, cardNumber);
    }

    public void ejectCard() {
        state.processCardEjection(this);
    }

    public void enterPin(String pin) {
        state.processPinEntry(this, pin);
    }

    public void withdrawRequest() {
        state.processWithdrawalRequest(this);
    }

    public void depositRequest() {
        state.processDepositRequest(this);
    }

    public void enterAmount(BigDecimal amount) {
        state.processAmountEntry(this, amount);
    }

    public void collectDeposit(BigDecimal amount) {
        state.processDepositCollection(this, amount);
    }

    public void transitionToState(ATMState nextState) {
        this.state = Objects.requireNonNull(nextState, "nextState cannot be null");
    }

    public ATMState getCurrentState() {
        return state;
    }

    public BankInterface getBankInterface() {
        return bank;
    }

    public CardProcessor getCardProcessor() {
        return cardProcessor;
    }

    public DepositBox getDepositBox() {
        return depositBox;
    }

    public CashDispenser getCashDispenser() {
        return cashDispenser;
    }

    public Keypad getKeypad() {
        return keypad;
    }

    public Display getDisplay() {
        return display;
    }
}
