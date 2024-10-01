package com.api.test.requests;


import com.api.test.configs.ConfigurationLoader;
import io.restassured.response.Response;

public class BookRequests extends BaseRequest {
  private final String BOOKS_URL = ConfigurationLoader.getProperty("booksUrl");

  public Response getAllBooks() {
    return sendRequest(BOOKS_URL, GET, null, null);
  }

  public Response getBookById(Object bookId) {
    return sendRequest(BOOKS_URL, GET, bookId, null);
  }

  public Response createBook(String jsonPayload) {
    return sendRequest(BOOKS_URL, POST, null, jsonPayload);
  }

  public Response updateBook(Object bookId, String jsonPayload) {
    return sendRequest(BOOKS_URL, PUT, bookId, jsonPayload);
  }

  public Response deleteBook(Object bookId) {
    return sendRequest(BOOKS_URL, DELETE, bookId, null);
  }
}
