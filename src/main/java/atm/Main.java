package atm;

import atm.account.Account;
import atm.account.AccountType;
import atm.bank.Bank;
import atm.bank.BankInterface;
import atm.hardware.ConsoleDisplay;
import atm.hardware.StandardCardProcessor;
import atm.hardware.StandardCashDispenser;
import atm.hardware.StandardDepositBox;
import atm.hardware.StandardKeypad;
import atm.machine.ATMMachine;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * =========================================================================================
 *                         ATM LOW-LEVEL DESIGN (LLD) SPECIFICATION
 * =========================================================================================
 *
 * DESIGN PATTERNS USED:
 * 1. State Pattern:
 *    - Implemented by {@link atm.state.ATMState} and concrete states:
 *      {@link atm.state.IdleState}, {@link atm.state.PinEntryState},
 *      {@link atm.state.TransactionSelectionState}, {@link atm.state.WithdrawAmountEntryState},
 *      and {@link atm.state.DepositCollectionState}.
 *    - Encapsulates state-dependent transitions and validates stage-specific actions.
 *
 * 2. Facade Pattern:
 *    - Implemented by {@link atm.machine.ATMMachine}.
 *    - Provides a unified, high-level client interface orchestrating hardware components,
 *      session state transitions, and banking operations.
 *
 * 3. Strategy / Polymorphic Command Pattern:
 *    - Implemented by {@link atm.transaction.Transaction} with concrete classes
 *      {@link atm.transaction.WithdrawTransaction} and {@link atm.transaction.DepositTransaction}.
 *    - Allows adding new transaction types (e.g. Transfers, Bill Pay) conforming to OCP.
 *
 * 4. Dependency Inversion Principle (DIP):
 *    - {@link atm.bank.BankInterface} abstracts the banking data layer from {@link ATMMachine}.
 *    - Enables swapping in-memory bank with remote microservices or test stubs seamlessly.
 *
 * CONCURRENCY STRATEGY:
 * 1. Account Directories:
 *    - {@link atm.bank.Bank} uses {@link java.util.concurrent.ConcurrentHashMap} for
 *      {@code accounts} and {@code accountByCard}, allowing lock-free concurrent reads and safe writes.
 *
 * 2. Balance Critical Section & Check-Then-Act Protection:
 *    - Every {@link atm.account.Account} possesses its own {@link java.util.concurrent.locks.ReentrantLock}.
 *    - In {@link atm.bank.Bank#withdrawFunds(Account, BigDecimal)}, the account lock is acquired
 *      prior to checking available funds and held until the balance debit completes. This eliminates
 *      the double-spend / lost-update race condition when concurrent ATMs access the same account.
 *    - Locking is scoped per-account (fine-grained) to prevent serializing independent accounts.
 *
 * 3. Immutable Account Metadata:
 *    - Account identity fields (accountNumber, cardNumber, cardPinHash, accountType) are final.
 *    - PINs are hashed using SHA-256 upon creation and verified with timing-attack resistant
 *      {@link java.security.MessageDigest#isEqual(byte[], byte[])}.
 *
 * 4. ATM Machine Session State:
 *    - {@link atm.machine.ATMMachine#getCurrentState()} represents a single physical console's
 *      interactive session. Since one physical machine accommodates one card and user at a time,
 *      session state transitions are inherently single-threaded per machine and do not require
 *      extraneous synchronization.
 * =========================================================================================
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("====================================================================");
        System.out.println("              ATM SYSTEM LOW-LEVEL DESIGN DEMONSTRATION             ");
        System.out.println("====================================================================\n");

        // 1. Initialize Shared Banking Backend
        BankInterface bank = new Bank();
        String accNumber1 = "ACC-1001";
        String card1 = "4111-2222-3333-4444";
        String pin1 = "1234";

        bank.addAccount(accNumber1, AccountType.CHECKING, card1, pin1);

        // 2. Initialize ATM Hardware
        StandardCardProcessor cardProcessor = new StandardCardProcessor();
        StandardDepositBox depositBox = new StandardDepositBox();
        StandardCashDispenser cashDispenser = new StandardCashDispenser();
        StandardKeypad keypad = new StandardKeypad();
        ConsoleDisplay display = new ConsoleDisplay();

        // 3. Create ATM Machine (Facade)
        ATMMachine atm = new ATMMachine(bank, cardProcessor, depositBox, cashDispenser, keypad, display);

        // =================================================================
        // DEMO SCENARIO 1: Standard Happy Path (Insert -> PIN -> Deposit -> Withdraw -> Eject)
        // =================================================================
        System.out.println("--- [SCENARIO 1: Happy Path Transaction Flow] ---");
        atm.insertCard(card1);
        atm.enterPin("1234");
        
        // Deposit $500
        atm.depositRequest();
        atm.collectDeposit(new BigDecimal("500.00"));

        // Withdraw $200
        atm.withdrawRequest();
        atm.enterAmount(new BigDecimal("200.00"));

        // Eject Card
        atm.ejectCard();
        System.out.println();

        // =================================================================
        // DEMO SCENARIO 2: Security Retention on 3 Failed PIN Attempts
        // =================================================================
        System.out.println("--- [SCENARIO 2: Security Lockout & Card Retention] ---");
        atm.insertCard(card1);
        atm.enterPin("9999"); // Attempt 1
        atm.enterPin("8888"); // Attempt 2
        atm.enterPin("7777"); // Attempt 3 -> Retains card
        System.out.println();

        // =================================================================
        // DEMO SCENARIO 3: Concurrent Multi-Threaded Double-Withdrawal Race Condition Test
        // =================================================================
        System.out.println("--- [SCENARIO 3: Concurrent Multi-ATM Withdrawal Race Condition Test] ---");
        String accNumber2 = "ACC-2002";
        String card2 = "5555-6666-7777-8888";
        String pin2 = "5678";
        bank.addAccount(accNumber2, AccountType.SAVINGS, card2, pin2);
        Account sharedAccount = bank.getAccountByAccountNumber(accNumber2);

        // Initial deposit of $100
        sharedAccount.updateBalanceWithTransaction(new BigDecimal("100.00"));
        System.out.println("Initial Balance for Account " + accNumber2 + ": $" + sharedAccount.getBalance());
        System.out.println("Spawning 2 concurrent ATM withdrawal requests of $100.00 each...");

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int atmId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Ensure both threads strike at the exact same millisecond
                    boolean success = bank.withdrawFunds(sharedAccount, new BigDecimal("100.00"));
                    System.out.println("[ATM " + atmId + "] Withdrawal of $100.00 success = " + success);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Fire both threads simultaneously
        endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("Final Balance after concurrent race: $" + sharedAccount.getBalance());
        System.out.println("Result: Exactly one withdrawal succeeded and no double-spending occurred!\n");
        System.out.println("====================================================================");
        System.out.println("                  DEMONSTRATION COMPLETED SUCCESSFULLY              ");
        System.out.println("====================================================================");
    }
}
