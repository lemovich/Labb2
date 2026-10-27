package org.example.budget;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Menu {

    private static final Logger logger = Logger.getLogger(Menu.class.getName());

    public static void printMenu() {
        System.out.println();
        System.out.println("Budgethanteraren");
        System.out.println("================");
        System.out.println("1. Lagg till transaktion");
        System.out.println("2. Visa alla transaktioner");
        System.out.println("3. Visa saldo och sammanstallning per kategori");
        System.out.println("4. Filtrera transaktioner");
        System.out.println("5. Spara till fil");
        System.out.println("e. Avsluta");
        System.out.print("Val: ");
    }

    public static void addTransactionFlow(Scanner scanner, Repository<Transaction> repository) {
        System.out.print("Datum (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Ogiltigt datumformat, avbryter.");
            logger.warning("Ogiltigt datum angivet: " + dateInput);
            return;
        }

        System.out.print("Kategori: ");
        String category = scanner.nextLine().trim();

        System.out.print("Belopp: ");
        String amountInput = scanner.nextLine().trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountInput);
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt belopp, avbryter.");
            logger.warning("Ogiltigt belopp angivet: " + amountInput);
            return;
        }

        System.out.print("Typ (INKOMST/UTGIFT): ");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        TransactionType type;
        try {
            type = TransactionType.valueOf(typeInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Ogiltig typ, ange INKOMST eller UTGIFT. Avbryter.");
            logger.warning("Ogiltig transaktionstyp angiven: " + typeInput);
            return;
        }

        try {
            Transaction transaction = new Transaction(date, category, amount, type);
            repository.add(transaction);
            System.out.println("Transaktion tillagd!");
            logger.info("Transaktion tillagd: " + transaction);
        } catch (InvalidTransactionException e) {
            System.out.println("Kunde inte lägga till transaktionen: " + e.getMessage());
            logger.warning("Ogiltig transaktion: " + e.getMessage());
        }
    }
    public static void listAllFlow(Repository<Transaction> repository) {
        List<Transaction> transactions = repository.findAll();

        if (transactions.isEmpty()) {
            System.out.println("Inga transaktioner registrerade.");
            return;
        }

        for (Transaction t : transactions) {
            System.out.println("- " + t.date() + " | " + t.category() + " | " + t.amount() + " kr | " + t.type());
        }
    }
    public static void summaryFlow(Repository<Transaction> repository, BudgetCalculator calculator) {
        List<Transaction> transactions = repository.findAll();

        BigDecimal balance = calculator.calculateBalance(transactions);
        Map<String, BigDecimal> byCategory = calculator.sumByCategory(transactions);

        System.out.println("Totalt saldo: " + balance + " kr");
        System.out.println();
        System.out.println("Summa per kategori:");

        if (byCategory.isEmpty()) {
            System.out.println("(inga transaktioner)");
        } else {
            for (Map.Entry<String, BigDecimal> entry : byCategory.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " kr");
            }
        }
    }
    public static void filterFlow(Scanner scanner, Repository<Transaction> repository) {
        System.out.println("Filtrera på:");
        System.out.println("1. Typ (INKOMST/UTGIFT)");
        System.out.println("2. Datumintervall");
        System.out.print("Val: ");
        String choice = scanner.nextLine().trim();

        List<Transaction> result;

        switch (choice) {
            case "1" -> result = filterByType(scanner, repository);
            case "2" -> result = filterByDateRange(scanner, repository);
            default -> {
                System.out.println("Ogiltigt val.");
                return;
            }
        }

        if (result.isEmpty()) {
            System.out.println("Inga transaktioner matchade filtret.");
            return;
        }

        for (Transaction t : result) {
            System.out.println("- " + t.date() + " | " + t.category() + " | " + t.amount() + " kr | " + t.type());
        }
    }

    private static List<Transaction> filterByType(Scanner scanner, Repository<Transaction> repository) {
        System.out.print("Typ (INKOMST/UTGIFT): ");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        try {
            TransactionType type = TransactionType.valueOf(typeInput);
            return repository.findWhere(t -> t.type() == type);
        } catch (IllegalArgumentException e) {
            System.out.println("Ogiltig typ.");
            logger.warning("Ogiltig typ angiven vid filtrering: " + typeInput);
            return List.of();
        }
    }

    private static List<Transaction> filterByDateRange(Scanner scanner, Repository<Transaction> repository) {
        System.out.print("Från datum (YYYY-MM-DD): ");
        String fromInput = scanner.nextLine().trim();
        System.out.print("Till datum (YYYY-MM-DD): ");
        String toInput = scanner.nextLine().trim();

        try {
            LocalDate from = LocalDate.parse(fromInput);
            LocalDate to = LocalDate.parse(toInput);
            return repository.findWhere(t -> !t.date().isBefore(from) && !t.date().isAfter(to));
        } catch (DateTimeParseException e) {
            System.out.println("Ogiltigt datumformat.");
            logger.warning("Ogiltigt datum angivet vid filtrering: " + fromInput + " / " + toInput);
            return List.of();
        }
    }
    public static void saveFlow(TransactionFileStorage storage, Repository<Transaction> repository, Path file) {
        try {
            storage.save(file, repository.findAll());
            System.out.println("Transaktioner sparade till " + file);
            logger.info("Sparade transaktioner till " + file);
        } catch (IOException e) {
            System.out.println("Kunde inte spara till fil: " + e.getMessage());
            logger.log(Level.SEVERE, "Kunde inte spara transaktioner: " + e.getMessage(), e);
        }
    }

}