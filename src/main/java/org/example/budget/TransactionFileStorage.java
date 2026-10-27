package org.example.budget;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TransactionFileStorage {

    public List<Transaction> load(Path file) throws IOException, FileFormatException {
        List<Transaction> transactions = new ArrayList<>();

        if (!Files.exists(file)) {
            return transactions; // ingen fil än - returnera tom lista, inget fel
        }

        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            transactions.add(parseLine(line));
        }

        return transactions;
    }

    private Transaction parseLine(String line) throws FileFormatException {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            throw new FileFormatException("Felaktigt antal fält i rad: " + line);
        }

        try {
            LocalDate date = LocalDate.parse(parts[0]);
            String category = parts[1];
            BigDecimal amount = new BigDecimal(parts[2]);
            TransactionType type = TransactionType.valueOf(parts[3]);

            return new Transaction(date, category, amount, type);
        } catch (NumberFormatException e) {
            throw new FileFormatException("Ogiltigt belopp i rad: " + line, e);
        } catch (DateTimeParseException e) {
            throw new FileFormatException("Ogiltigt datum i rad: " + line, e);
        } catch (IllegalArgumentException e) {
            throw new FileFormatException("Ogiltig transaktionstyp i rad: " + line, e);
        }
    }

    public void save(Path file, List<Transaction> transactions) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Transaction t : transactions) {
            lines.add(t.date() + "," + t.category() + "," + t.amount() + "," + t.type());
        }
        Files.write(file, lines);
    }
}