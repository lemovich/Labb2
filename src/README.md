# Budgethanteraren

Ett konsolbaserat Java-program för att registrera, lagra och summera privata
transaktioner (inkomster och utgifter). Programmet sparar data till en CSV-fil
och kan läsa in den igen vid nästa körning.

## Funktioner

- Lägg till transaktion (datum, kategori, belopp, typ)
- Lista alla transaktioner
- Visa en sammanfattning: total balans samt summa per kategori
- Filtrera transaktioner på typ (inkomst/utgift) eller datumintervall
- Spara till fil manuellt eller automatiskt vid avslut
- Loggning av fel och viktiga händelser till `budget.log`

## Hur man kör programmet

1. Öppna projektet i IntelliJ (eller annan IDE med Maven-stöd).
2. Kör klassen `BudgetApp` (`src/main/java/org/example/budget/BudgetApp.java`).
3. Följ menyn i konsolen genom att skriva siffran för önskat val.
4. Vid avslut sparas transaktionerna automatiskt till fil.

Alternativt, via terminalen i projektmappen:
`mvn compile exec:java -Dexec.mainClass="org.example.budget.BudgetApp`


## Teknisk översikt

Programmet är uppbyggt enligt följande:

- **`TransactionType`** – enum som beskriver om en transaktion är en inkomst
  eller en utgift.
- **`Transaction`** – en record som representerar en enskild transaktion.
  Validering av indata (null-kontroller, att kategorin inte är tom, att
  beloppet är större än noll) sker i en compact constructor och kastar
  `InvalidTransactionException` vid ogiltiga värden.
- **`Repository<T>`** – en generisk klass som lagrar objekt av valfri typ.
  Används här för transaktioner, men är inte beroende av `Transaction`
  specifikt.
- **`TransactionFileStorage`** – hanterar läsning och skrivning till en
  CSV-fil. Fel vid inläsning (felaktigt formaterade rader, ogiltiga datum
  eller belopp) fångas och kastas vidare som `FileFormatException`.
- **`BudgetCalculator`** – beräknar total balans och summa per kategori med
  hjälp av Stream API (`Collectors.groupingBy` och `Collectors.reducing`).
  `BigDecimal` används istället för `double` för att undvika
  avrundningsfel vid pengaberäkningar.
- **`Menu`** – innehåller all logik för menyflödet och användarinteraktionen.
- **`BudgetApp`** – programmets startpunkt, kopplar ihop övriga klasser och
  initierar loggning.

Loggning sker via `java.util.logging` och skrivs till filen `budget.log`
istället för konsolen, för att hålla konsolgränssnittet rent från
loggmeddelanden under körning.

## Testning

Projektet har en JUnit 5-testsvit som täcker:

- **`RepositoryTest`** – att lägga till och hämta objekt, att `findAll()`
  returnerar en kopia (inte den interna listan), samt filtrering med
  `findWhere()`.
- **`BudgetCalculatorTest`** – korrekt balansberäkning (inklusive tomma
  listor och enbart utgifter) samt summering per kategori.
- **`TransactionTest`** – att konstruktorn kastar `InvalidTransactionException`
  för varje ogiltig kombination av indata (null-datum, tom kategori,
  belopp som inte är större än noll, saknad typ), samt att giltiga värden
  skapar en korrekt transaktion.

## Dokumenterad bugg

Under testningen av `Repository<T>` upptäcktes ett fel i testet
`findWhere_filtersCorrectly`: `AssertionFailedError: Expected :2 Actual :1`


**Felsökning:** Testet fyllde en `Repository<String>` med orden `"Apelsin"`,
`"Äpple"` och `"Banan"`, och förväntade sig att filtret
`s -> s.startsWith("A")` skulle matcha två ord. Genom att läsa
assertion-meddelandet och gå igenom testdatan rad för rad upptäcktes att
endast ett ord faktiskt matchade.

**Grundorsak:** `"Äpple".startsWith("A")` returnerar `false` i Java, eftersom
`Ä` och `A` är två skilda Unicode-tecken. Felet låg alltså inte i
`Repository`-klassens logik, utan i testdatan – ordet som var tänkt att
matcha gjorde det aldrig på grund av bokstaven Ä.

**Fix:** Testdatan ändrades så att `"Äpple"` byttes ut mot `"Apelsin"` (ett
ord som faktiskt börjar på `A`), vilket gjorde att testet gav rätt resultat
och speglade avsikten med testet korrekt.



