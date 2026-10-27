package org.example.budget;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void addAndFindAll_returnsAddedItems() {
        // Arrange
        Repository<String> repository = new Repository<>();

        // Act
        repository.add("Äpple");
        repository.add("Banan");
        List<String> result = repository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Äpple"));
        assertTrue(result.contains("Banan"));
    }

    @Test
    void findAll_onEmptyRepository_returnsEmptyList() {
        // Arrange
        Repository<String> repository = new Repository<>();

        // Act
        List<String> result = repository.findAll();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsCopyNotInternalList() {
        // Arrange
        Repository<String> repository = new Repository<>();
        repository.add("Äpple");

        // Act
        List<String> result = repository.findAll();
        result.add("Smuggla in en banan"); // försöker ändra den returnerade listan

        // Assert
        assertEquals(1, repository.findAll().size()); // interna listan ska vara opåverkad
    }

    @Test
    void findWhere_filtersCorrectly() {
        // Arrange
        Repository<String> repository = new Repository<>();
        repository.add("Äpple");
        repository.add("Banan");
        repository.add("Ananas");

        // Act
        List<String> result = repository.findWhere(s -> s.startsWith("A"));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Äpple"));
        assertTrue(result.contains("Ananas"));
    }

    @Test
    void findWhere_noMatches_returnsEmptyList() {
        // Arrange
        Repository<String> repository = new Repository<>();
        repository.add("Äpple");

        // Act
        List<String> result = repository.findWhere(s -> s.startsWith("Z"));

        // Assert
        assertTrue(result.isEmpty());
    }
}