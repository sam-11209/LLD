package atm;

import atm.account.Account;
import atm.account.AccountType;
import atm.transaction.DepositTransaction;
import atm.transaction.TransactionType;
import atm.transaction.WithdrawTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("DepositTransaction should validate and execute successfully")
    void testDepositTransaction() {
        Account account = new Account("A1", AccountType.CHECKING, "C1", "1234");
        DepositTransaction tx = new DepositTransaction(account, new BigDecimal("150.00"));

        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertTrue(tx.validateTransaction());

        tx.executeTransaction();
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    @DisplayName("WithdrawTransaction should throw exception when initialized with insufficient funds")
    void testWithdrawTransactionInsufficientFunds() {
        Account account = new Account("A1", AccountType.SAVINGS, "C1", "1234");
        account.updateBalanceWithTransaction(new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class, () ->
                new WithdrawTransaction(account, new BigDecimal("100.00"))
        );
    }

    @Test
    @DisplayName("WithdrawTransaction should execute successfully with sufficient funds")
    void testWithdrawTransactionSuccess() {
        Account account = new Account("A1", AccountType.SAVINGS, "C1", "1234");
        account.updateBalanceWithTransaction(new BigDecimal("200.00"));

        WithdrawTransaction tx = new WithdrawTransaction(account, new BigDecimal("80.00"));
        assertEquals(TransactionType.WITHDRAW, tx.getType());
        assertTrue(tx.validateTransaction());

        tx.executeTransaction();
        assertEquals(new BigDecimal("120.00"), account.getBalance());
    }
}
