package org.example.budget;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

public class BudgetApp {

    private static final Logger logger = Logger.getLogger(BudgetApp.class.getName());
    private static final Path DATA_FILE = Path.of("transaktioner.csv");

    static void main() {

        setupLogging();

        Repository<Transaction> repository = new Repository<>();
        TransactionFileStorage storage = new TransactionFileStorage();
        BudgetCalculator calculator = new BudgetCalculator();
        Scanner scanner = new Scanner(System.in);

        loadTransactions(storage, repository);

        boolean running = true;
        while (running) {
            Menu.printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> Menu.addTransactionFlow(scanner, repository);
                case "2" -> Menu.listAllFlow(repository);
                case "3" -> Menu.summaryFlow(repository, calculator);
                case "4" -> Menu.filterFlow(scanner, repository);
                case "5" -> Menu.saveFlow(storage, repository, DATA_FILE);
                case "e", "E" -> {
                    saveTransactions(storage, repository);
                    running = false;
                    System.out.println("Avslutar programmet.");
                }
                default -> System.out.println("Ogiltigt val, försök igen.");
            }
        }
    }

    private static void setupLogging() {
        try {
            FileHandler fileHandler = new FileHandler("budget.log", true); // true = bygg på filen, skriv inte över
            fileHandler.setFormatter(new SimpleFormatter());

            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);

            // Ta bort standard-konsolutskriften så loggarna inte stör menyn
            for (Handler handler : rootLogger.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }
        } catch (IOException e) {
            System.out.println("Kunde inte sätta upp loggfil, loggar till konsolen istället.");
        }
    }

    private static void loadTransactions(TransactionFileStorage storage, Repository<Transaction> repository) {
        try {
            List<Transaction> loaded = storage.load(DATA_FILE);
            for (Transaction t : loaded) {
                repository.add(t);
            }
            logger.info("Laddade " + loaded.size() + " transaktioner från " + DATA_FILE);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Kunde inte läsa transaktionsfilen: " + e.getMessage(), e);
            System.out.println("Kunde inte läsa transaktionsfilen, startar med ett tomt register.");
        } catch (FileFormatException e) {
            logger.log(Level.WARNING, "Felformaterad fil: " + e.getMessage(), e);
            System.out.println("Filen innehöll felaktig data: " + e.getMessage());
        }
    }

    private static void saveTransactions(TransactionFileStorage storage, Repository<Transaction> repository) {
        try {
            storage.save(DATA_FILE, repository.findAll());
            logger.info("Sparade transaktioner till " + DATA_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Kunde inte spara transaktioner: " + e.getMessage(), e);
            System.out.println("Kunde inte spara till fil: " + e.getMessage());
        }
    }
}