package atm;

import atm.account.Account;
import atm.account.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    @DisplayName("Should initialize account with zero balance and securely validate PIN")
    void testAccountInitializationAndPinValidation() {
        Account account = new Account("ACC-001", AccountType.CHECKING, "1111-2222-3333-4444", "1234");

        assertEquals("ACC-001", account.getAccountNumber());
        assertEquals("1111-2222-3333-4444", account.getCardNumber());
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertEquals(BigDecimal.ZERO, account.getBalance());

        assertTrue(account.validatePin("1234"));
        assertFalse(account.validatePin("0000"));
        assertFalse(account.validatePin(null));
        assertFalse(account.validatePin(""));
    }

    @Test
    @DisplayName("Should correctly update balance with thread-safe lock")
    void testBalanceUpdates() {
        Account account = new Account("ACC-002", AccountType.SAVINGS, "5555-6666-7777-8888", "4321");

        account.updateBalanceWithTransaction(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), account.getBalance());

        account.updateBalanceWithTransaction(new BigDecimal("-50.25"));
        assertEquals(new BigDecimal("200.25"), account.getBalance());
    }

    @Test
    @DisplayName("Should handle 100 concurrent deposit updates without data loss")
    void testConcurrentBalanceUpdates() throws InterruptedException {
        Account account = new Account("ACC-003", AccountType.CHECKING, "9999-8888-7777-6666", "1111");
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    account.updateBalanceWithTransaction(new BigDecimal("10.00"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }
}
