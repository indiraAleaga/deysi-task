package com.api.test.repository;

import com.api.test.models.Author;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class AuthorRepository {

    private List<Author> authors;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String DATA_URL = "data/authors/";
    private final Faker faker = new Faker();

    public void loadAuthors(String resourceName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(String.format("%s%s", DATA_URL, resourceName));
        try {
            authors = objectMapper.readValue(is, new TypeReference<List<Author>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Author> getAllAuthors() {
        return authors;
    }

    public Author getFakeNewAuthor() {

        return Author.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .idBook(faker.number().numberBetween(0, 1000))
                .build();
    }
}
