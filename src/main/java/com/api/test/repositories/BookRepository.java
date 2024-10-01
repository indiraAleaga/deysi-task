package com.api.test.repositories;

import com.api.test.models.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookRepository {
  private static final String DATA_URL = "data/books/";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Faker faker = new Faker();
  private List<Book> books;

  // Load books from JSON file
  public void loadBooks(String resourceName) {
    InputStream is =
        getClass()
            .getClassLoader()
            .getResourceAsStream(String.format("%s%s", DATA_URL, resourceName));
    try {
      books = objectMapper.readValue(is, new TypeReference<List<Book>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Book> getAllBooks() {
    return books;
  }

  public Book getFakeNewBook() {

    Instant pastInstant = faker.date().past(365, TimeUnit.DAYS).toInstant();
    String formattedPublishDate =
        DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(pastInstant);

    return Book.builder()
        .title(faker.book().title())
        .description(faker.lorem().paragraph())
        .pageCount(faker.number().numberBetween(0, 1000))
        .excerpt(faker.lorem().paragraph())
        .publishDate(formattedPublishDate)
        .build();
  }
}
