package com.api.test.requests;


import com.api.test.configs.ConfigurationLoader;
import io.restassured.response.Response;

public class AuthorRequests extends BaseRequest {
  private final String AUTHORS_URL = ConfigurationLoader.getProperty("authorsUrl");

  public Response getAllAuthors() {
    return sendRequest(AUTHORS_URL, GET, null, null);
  }

  public Response getAuthorById(Object authorId) {
    return sendRequest(AUTHORS_URL, GET, authorId, null);
  }

  public Response createAuthor(String jsonPayload) {
    return sendRequest(AUTHORS_URL, POST, null, jsonPayload);
  }

  public Response updateAuthor(Object authorId, String jsonPayload) {
    return sendRequest(AUTHORS_URL, PUT, authorId, jsonPayload);
  }

  public Response deleteAuthor(Object authorId) {
    return sendRequest(AUTHORS_URL, DELETE, authorId, null);
  }
}
