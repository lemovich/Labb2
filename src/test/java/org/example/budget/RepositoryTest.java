package org.example.budget;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void addAndFindAll_returnsAddedItems() {
        Repository<String> repository = new Repository<>();

        repository.add("Äpple");
        repository.add("Banan");
        List<String> result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains("Äpple"));
        assertTrue(result.contains("Banan"));
    }

    @Test
    void findAll_onEmptyRepository_returnsEmptyList() {
        Repository<String> repository = new Repository<>();

        List<String> result = repository.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsCopyNotInternalList() {
        Repository<String> repository = new Repository<>();
        repository.add("Äpple");

        List<String> result = repository.findAll();
        result.add("Smuggla in en banan"); // försöker ändra den returnerade listan

        assertEquals(1, repository.findAll().size()); // interna listan ska vara opåverkad
    }

    @Test
    void findWhere_filtersCorrectly() {
        Repository<String> repository = new Repository<>();
        repository.add("Apelsin");
        repository.add("Banan");
        repository.add("Ananas");

        List<String> result = repository.findWhere(s -> s.startsWith("A"));

        assertEquals(2, result.size());
        assertTrue(result.contains("Apelsin"));
        assertTrue(result.contains("Ananas"));
    }

    @Test
    void findWhere_noMatches_returnsEmptyList() {
        Repository<String> repository = new Repository<>();
        repository.add("Apelsin");

        List<String> result = repository.findWhere(s -> s.startsWith("Z"));

        assertTrue(result.isEmpty());
    }
}