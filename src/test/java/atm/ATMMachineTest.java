package atm;

import atm.account.Account;
import atm.account.AccountType;
import atm.bank.Bank;
import atm.bank.BankInterface;
import atm.hardware.StandardCardProcessor;
import atm.hardware.StandardCashDispenser;
import atm.hardware.StandardDepositBox;
import atm.hardware.StandardKeypad;
import atm.machine.ATMMachine;
import atm.state.IdleState;
import atm.state.PinEntryState;
import atm.state.TransactionSelectionState;
import atm.state.WithdrawAmountEntryState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ATMMachineTest {

    private BankInterface bank;
    private StandardCardProcessor cardProcessor;
    private StandardDepositBox depositBox;
    private StandardCashDispenser cashDispenser;
    private StandardKeypad keypad;
    private List<String> displayMessages;
    private ATMMachine atm;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.addAccount("ACC-999", AccountType.CHECKING, "CARD-999", "4321");

        cardProcessor = new StandardCardProcessor();
        depositBox = new StandardDepositBox();
        cashDispenser = new StandardCashDispenser();
        keypad = new StandardKeypad();
        displayMessages = new ArrayList<>();

        atm = new ATMMachine(
                bank,
                cardProcessor,
                depositBox,
                cashDispenser,
                keypad,
                displayMessages::add
        );
    }

    @Test
    @DisplayName("Should transition from Idle to PinEntry on valid card")
    void testCardInsertionValid() {
        assertTrue(atm.getCurrentState() instanceof IdleState);

        atm.insertCard("CARD-999");
        assertTrue(atm.getCurrentState() instanceof PinEntryState);
        assertEquals("CARD-999", cardProcessor.getCardNumber());
    }

    @Test
    @DisplayName("Should stay in IdleState on invalid card")
    void testCardInsertionInvalid() {
        atm.insertCard("INVALID-CARD");
        assertTrue(atm.getCurrentState() instanceof IdleState);
        assertNull(cardProcessor.getCardNumber());
    }

    @Test
    @DisplayName("Should retain card and reset to IdleState after 3 consecutive wrong PIN attempts")
    void testCardRetentionAfterThreeFailedPinAttempts() {
        atm.insertCard("CARD-999");
        assertTrue(atm.getCurrentState() instanceof PinEntryState);

        atm.enterPin("0000"); // 1st failure
        assertTrue(atm.getCurrentState() instanceof PinEntryState);

        atm.enterPin("1111"); // 2nd failure
        assertTrue(atm.getCurrentState() instanceof PinEntryState);

        atm.enterPin("2222"); // 3rd failure -> retain & return to Idle
        assertTrue(atm.getCurrentState() instanceof IdleState);
        assertNull(cardProcessor.getCardNumber(), "Card should be retained and no longer held as active card");
    }

    @Test
    @DisplayName("Should allow transaction selection and perform deposit")
    void testSuccessfulDepositFlow() {
        atm.insertCard("CARD-999");
        atm.enterPin("4321");
        assertTrue(atm.getCurrentState() instanceof TransactionSelectionState);

        atm.depositRequest();
        atm.collectDeposit(new BigDecimal("350.00"));

        Account acc = bank.getAccountByCard("CARD-999");
        assertEquals(new BigDecimal("350.00"), acc.getBalance());
        assertTrue(atm.getCurrentState() instanceof TransactionSelectionState);
    }

    @Test
    @DisplayName("Should allow withdrawal when sufficient funds are present")
    void testSuccessfulWithdrawalFlow() {
        Account acc = bank.getAccountByCard("CARD-999");
        acc.updateBalanceWithTransaction(new BigDecimal("500.00"));

        atm.insertCard("CARD-999");
        atm.enterPin("4321");

        atm.withdrawRequest();
        assertTrue(atm.getCurrentState() instanceof WithdrawAmountEntryState);

        atm.enterAmount(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("300.00"), acc.getBalance());
        assertTrue(atm.getCurrentState() instanceof TransactionSelectionState);

        atm.ejectCard();
        assertTrue(atm.getCurrentState() instanceof IdleState);
    }
}
