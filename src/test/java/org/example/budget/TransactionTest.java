package org.example.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void constructor_withNullDate_throwsInvalidTransactionException() {
        // Arrange + Act + Assert
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(null, "Mat", new BigDecimal("100"), TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withNullCategory_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), null, new BigDecimal("100"), TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withBlankCategory_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), "   ", new BigDecimal("100"), TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withNullAmount_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), "Mat", null, TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withZeroAmount_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), "Mat", BigDecimal.ZERO, TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withNegativeAmount_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), "Mat", new BigDecimal("-50"), TransactionType.UTGIFT);
        });
    }

    @Test
    void constructor_withNullType_throwsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> {
            new Transaction(LocalDate.now(), "Mat", new BigDecimal("100"), null);
        });
    }

    @Test
    void constructor_withValidValues_createsTransactionSuccessfully() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 10, 1);
        BigDecimal amount = new BigDecimal("250.50");

        // Act
        Transaction transaction = new Transaction(date, "Lön", amount, TransactionType.INKOMST);

        // Assert
        assertEquals(date, transaction.date());
        assertEquals("Lön", transaction.category());
        assertEquals(0, amount.compareTo(transaction.amount()));
        assertEquals(TransactionType.INKOMST, transaction.type());
    }
}