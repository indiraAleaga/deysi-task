package com.api.test.requests;

import static io.restassured.RestAssured.given;

import com.api.test.config.ConfigurationLoader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BooksRequests {
  private final String BOOKS_URL = ConfigurationLoader.getProperty("booksUrl");

  public Response getAllBooks() {
    return given().when().get(BOOKS_URL);
  }

  public Response getBookById(int bookId) {
    return given()
        .contentType(ContentType.JSON)
        .when()
        .get(String.format("%s/%s", BOOKS_URL, bookId));
  }

  public Response createBook(String jsonPayload) {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .body(jsonPayload)
        .when()
        .post(BOOKS_URL);
  }

  public Response updateBook(int bookId, String jsonPayload) {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .body(jsonPayload)
        .when()
        .put(String.format("%s/%s", BOOKS_URL, bookId));
  }

  public Response deleteBook(int bookId) {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .delete(String.format("%s/%s", BOOKS_URL, bookId));
  }
}
