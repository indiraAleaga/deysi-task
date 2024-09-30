package com.api.test.requests;

import static io.restassured.RestAssured.given;

import com.api.test.config.ConfigurationLoader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AuthorRequests {
  private final String AUTHORS_URL = ConfigurationLoader.getProperty("authorsUrl");

  public Response getAllAuthors() {
    return given().when().get(AUTHORS_URL);
  }

  public Response getAuthorById(int bookId) {
    return given()
        .contentType(ContentType.JSON)
        .when()
        .get(String.format("%s/%s", AUTHORS_URL, bookId));
  }

  public Response createAuthor(String jsonPayload) {
    return given().contentType(ContentType.JSON).body(jsonPayload).when().post(AUTHORS_URL);
  }

  public Response updateAuthor(int bookId, String jsonPayload) {
    return given()
        .contentType(ContentType.JSON)
        .body(jsonPayload)
        .when()
        .put(String.format("%s/%s", AUTHORS_URL, bookId));
  }

  public Response deleteAuthor(int bookId) {
    return given()
        .contentType(ContentType.JSON)
        .when()
        .delete(String.format("%s/%s", AUTHORS_URL, bookId));
  }
}
