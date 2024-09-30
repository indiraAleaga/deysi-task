package com.api.test.tests;

import com.api.test.models.Author;
import com.api.test.repository.AuthorRepository;
import com.api.test.requests.AuthorRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AuthorTests extends BaseApiTest {

  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private AuthorRepository authorRepository;
  private Author expectedAuthor;
  private AuthorRequests authorRequests;

  @BeforeClass
  public void beforeClass() {

    authorRequests = new AuthorRequests();
    authorRepository = new AuthorRepository();
    authorRepository.loadAuthors("authors.json");
    expectedAuthor = authorRepository.getAllAuthors().get(0);
  }

  @Test(description = "Get all authors")
  public void testGetAllAuthors() {
    Response response =
        authorRequests.getAllAuthors().then().statusCode(HttpStatus.SC_OK).extract().response();

    Assert.assertFalse(
        response.jsonPath().getList("id").isEmpty(), "Authors list should not be empty");
  }

  @Test(description = "Get an author by ID")
  public void testGetAuthorById() {
    int authorId = 1;
    Response response =
        authorRequests.getAuthorById(authorId).then().statusCode(200).extract().response();

    Assert.assertEquals(response.jsonPath().getInt("id"), authorId, "Author ID should match");
  }
}
