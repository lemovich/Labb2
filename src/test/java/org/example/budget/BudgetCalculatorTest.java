package org.example.budget;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class BudgetCalculatorTest {

    private final BudgetCalculator calculator = new BudgetCalculator();

    @Test
    void calculateBalance_withIncomeAndExpense_returnsCorrectNetBalance() {
        List<Transaction> transactions = List.of(
                new Transaction(LocalDate.of(2026, 1, 1), "Lön", new BigDecimal("20000"), TransactionType.INKOMST),
                new Transaction(LocalDate.of(2026, 1, 2), "Mat", new BigDecimal("3000"), TransactionType.UTGIFT)
        );

        BigDecimal balance = calculator.calculateBalance(transactions);

        assertEquals(0, new BigDecimal("17000").compareTo(balance));
    }

    @Test
    void calculateBalance_withEmptyList_returnsZero() {
        List<Transaction> transactions = List.of();

        BigDecimal balance = calculator.calculateBalance(transactions);

        assertEquals(0, BigDecimal.ZERO.compareTo(balance));
    }

    @Test
    void calculateBalance_withOnlyExpenses_returnsNegativeBalance() {
        List<Transaction> transactions = List.of(
                new Transaction(LocalDate.of(2026, 1, 1), "Hyra", new BigDecimal("8000"), TransactionType.UTGIFT)
        );

        BigDecimal balance = calculator.calculateBalance(transactions);

        assertEquals(0, new BigDecimal("-8000").compareTo(balance));
    }

    @Test
    void sumByCategory_groupsTransactionsCorrectly() {
        List<Transaction> transactions = List.of(
                new Transaction(LocalDate.of(2026, 1, 1), "Mat", new BigDecimal("500"), TransactionType.UTGIFT),
                new Transaction(LocalDate.of(2026, 1, 2), "Mat", new BigDecimal("300"), TransactionType.UTGIFT),
                new Transaction(LocalDate.of(2026, 1, 3), "Lön", new BigDecimal("20000"), TransactionType.INKOMST)
        );

        Map<String, BigDecimal> result = calculator.sumByCategory(transactions);

        assertEquals(0, new BigDecimal("-800").compareTo(result.get("Mat")));
        assertEquals(0, new BigDecimal("20000").compareTo(result.get("Lön")));
    }

    @Test
    void sumByCategory_withEmptyList_returnsEmptyMap() {
        List<Transaction> transactions = List.of();

        Map<String, BigDecimal> result = calculator.sumByCategory(transactions);

        assertTrue(result.isEmpty());
    }
}