package atm;

import atm.account.Account;
import atm.account.AccountType;
import atm.bank.Bank;
import atm.bank.BankInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    private BankInterface bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.addAccount("ACC-101", AccountType.CHECKING, "CARD-101", "1234");
    }

    @Test
    @DisplayName("Should validate registered card and PIN")
    void testValidateCardAndPin() {
        assertTrue(bank.validateCard("CARD-101"));
        assertFalse(bank.validateCard("CARD-999"));

        assertTrue(bank.checkPin("CARD-101", "1234"));
        assertFalse(bank.checkPin("CARD-101", "0000"));
        assertFalse(bank.checkPin("CARD-999", "1234"));
    }

    @Test
    @DisplayName("Should execute atomic check-then-act withdrawal and prevent overdraft")
    void testWithdrawFunds() {
        Account account = bank.getAccountByCard("CARD-101");
        account.updateBalanceWithTransaction(new BigDecimal("100.00"));

        // Sufficient funds
        boolean success = bank.withdrawFunds(account, new BigDecimal("40.00"));
        assertTrue(success);
        assertEquals(new BigDecimal("60.00"), account.getBalance());

        // Insufficient funds
        boolean overdraw = bank.withdrawFunds(account, new BigDecimal("70.00"));
        assertFalse(overdraw);
        assertEquals(new BigDecimal("60.00"), account.getBalance());

        // Negative or zero amount rejection
        assertFalse(bank.withdrawFunds(account, BigDecimal.ZERO));
        assertFalse(bank.withdrawFunds(account, new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Should prevent double-spending in concurrent withdrawals on same account")
    void testConcurrentWithdrawalsPreventDoubleSpend() throws InterruptedException {
        Account account = bank.getAccountByCard("CARD-101");
        account.updateBalanceWithTransaction(new BigDecimal("100.00"));

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);

        // 10 threads trying to withdraw $100 concurrently; only exactly 1 must succeed
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    if (bank.withdrawFunds(account, new BigDecimal("100.00"))) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "Exactly one concurrent withdrawal must succeed");
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()), "Remaining balance should be 0");
    }
}
