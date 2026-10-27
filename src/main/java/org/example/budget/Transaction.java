package org.example.budget;
import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(LocalDate date, String category, BigDecimal amount, TransactionType type) {

    public Transaction {
        if (date == null) {
            throw new InvalidTransactionException("Datum måste anges.");
        }
        if (category == null || category.isBlank()) {
            throw new InvalidTransactionException("Kategori får inte vara tom.");
        }
        if (type == null) {
            throw new InvalidTransactionException("Transaktionstyp måste anges.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Beloppet måste vara större än noll.");
        }
    }
}