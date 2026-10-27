package org.example.budget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetCalculator {

    public BigDecimal calculateBalance(List<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            balance = balance.add(signedAmount(t));
        }
        return balance;
    }

    public Map<String, BigDecimal> sumByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::category,
                        Collectors.reducing(BigDecimal.ZERO, this::signedAmount, BigDecimal::add)
                ));
    }

    private BigDecimal signedAmount(Transaction t) {
        return t.type() == TransactionType.INKOMST ? t.amount() : t.amount().negate();
    }
}