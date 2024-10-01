package com.api.test.tests;

import static com.api.test.constants.ApiTestsConstants.INVALID_ID_DATA_TYPE;
import static com.api.test.constants.ApiTestsConstants.NON_EXISTENT_ID;

import com.api.test.models.Author;
import com.api.test.repositories.AuthorRepository;
import com.api.test.requests.AuthorRequests;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AuthorTests extends BaseApiTest {

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
    Author responseAuthor =
        authorRequests
            .getAuthorById(expectedAuthor.getId())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response()
            .as(Author.class);
    Assert.assertNotNull(responseAuthor, "Author not found in repository");
    Assert.assertEquals(
        responseAuthor.getId(),
        expectedAuthor.getId(),
        String.format(
            "The expected author id is [%s], but we got [%s]",
            expectedAuthor.getId(), responseAuthor.getId()));
    Assert.assertEquals(
        responseAuthor.getFirstName(),
        expectedAuthor.getFirstName(),
        String.format(
            "The expected author FirstName is [%s], but we got [%s]",
            expectedAuthor.getFirstName(), responseAuthor.getFirstName()));
    Assert.assertEquals(
        responseAuthor.getLastName(),
        expectedAuthor.getLastName(),
        String.format(
            "The expected author LastName is [%s], but we got [%s]",
            expectedAuthor.getLastName(), responseAuthor.getLastName()));
    Assert.assertEquals(
        responseAuthor.getIdBook(),
        expectedAuthor.getIdBook(),
        String.format(
            "The expected author book reference is [%s], but we got [%s]",
            expectedAuthor.getIdBook(), responseAuthor.getIdBook()));
  }

  @Test(description = "Get an author by NON-Existent ID")
  public void testGetAuthorByIdNotFound() {

    Response response =
        authorRequests
            .getAuthorById(NON_EXISTENT_ID)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Get an author by Invalid DataType Id")
  public void testGetAuthorByInvalidIdDataType() {

    Response response =
        authorRequests
            .getAuthorById(INVALID_ID_DATA_TYPE)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();

    verifyError.verifyErrorInvalidIdType(response, INVALID_ID_DATA_TYPE);
  }

  @Test(description = "Create an Author with valid data")
  public void testCreateAuthor() {
    Author newAuthor = authorRepository.getFakeNewAuthor();
    String payLoad = gson.toJson(newAuthor);
    Response response = authorRequests.createAuthor(payLoad).then().extract().response();
    Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK); // HTTP 201 Created
    Assert.assertNotNull(response.jsonPath().getString("id"), "Should generate an id");

    Author createdAuthor = response.as(Author.class);
    Author responseAuthor =
        authorRequests
            .getAuthorById(createdAuthor.getId())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response()
            .as(Author.class);
    Assert.assertNotNull(responseAuthor, "Author not found in repository");
    Assert.assertEquals(responseAuthor.getId(), createdAuthor.getId());
  }

  @Test(description = "Create an Author with invalid data type")
  public void testCreateAuthorWithInvalidDataType() {
    Author newAuthor = authorRepository.getFakeNewAuthor();
    newAuthor.setIdBook(INVALID_ID_DATA_TYPE);
    String payLoad = gson.toJson(newAuthor);
    Response response = authorRequests.createAuthor(payLoad).then().extract().response();
    Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST); // HTTP 201 Created
    verifyError.verifyErrorInvalidIdBookType(response);
  }

  @Test(description = "Update Author with valid data")
  public void updateAuthor() {
    Author author = authorRepository.getFakeNewAuthor();
    author.setId(expectedAuthor.getId());
    String payload = gson.toJson(author);

    Author updatedAuthor =
        authorRequests
            .updateAuthor(author.getId(), payload)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response()
            .as(Author.class);

    Assert.assertNotNull(updatedAuthor, "Author not found in repository");
    Assert.assertEquals(
        updatedAuthor.getId(),
        author.getId(),
        String.format(
            "The expected author id is [%s], but we got [%s]",
            author.getId(), updatedAuthor.getId()));
    Assert.assertEquals(
        updatedAuthor.getFirstName(),
        author.getFirstName(),
        String.format(
            "The expected author FirstName is [%s], but we got [%s]",
            author.getFirstName(), updatedAuthor.getFirstName()));
    Assert.assertEquals(
        updatedAuthor.getLastName(),
        author.getLastName(),
        String.format(
            "The expected author LastName is [%s], but we got [%s]",
            author.getLastName(), updatedAuthor.getLastName()));
    Assert.assertEquals(
        updatedAuthor.getIdBook(),
        author.getIdBook(),
        String.format(
            "The expected author book reference is [%s], but we got [%s]",
            author.getIdBook(), updatedAuthor.getIdBook()));
  }

  @Test(description = "Update Author with mismatch id ")
  public void updateAuthorMistMatchID() {
    Integer misMatchId = ((Integer) expectedAuthor.getId()) + 1;
    Author author = authorRepository.getFakeNewAuthor();
    author.setId(expectedAuthor.getId());
    String payload = gson.toJson(author);
    authorRequests.updateAuthor(misMatchId, payload).then().statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test(description = "Update Author with NON-existent id ")
  public void updateAuthorNonExistentID() {
    Author author = authorRepository.getFakeNewAuthor();
    author.setId(expectedAuthor.getId());
    String payload = gson.toJson(author);
    Response response =
        authorRequests
            .updateAuthor(NON_EXISTENT_ID, payload)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Update Author with invalid data type  ")
  public void updateAuthorInvalidDataType() {
    Author author = authorRepository.getFakeNewAuthor();
    author.setId(expectedAuthor.getId());
    author.setIdBook(INVALID_ID_DATA_TYPE);
    String payload = gson.toJson(author);
    Response response =
        authorRequests
            .updateAuthor(author.getId(), payload)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();
    verifyError.verifyErrorInvalidIdBookType(response);
  }

  @Test(description = "Delete author with valid Id")
  public void testDeleteAuthorWithValidId() {
    authorRequests.deleteAuthor(expectedAuthor.getId()).then().statusCode(HttpStatus.SC_OK);
  }

  @Test(description = "Delete author with NON-existent ID")
  public void testDeleteAuthorWithNonExistentID() {
    Response response =
        authorRequests
            .deleteAuthor(NON_EXISTENT_ID)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Delete author with invalidIDFormat")
  public void testDeleteAuthorWithInvalidIdFormat() {
    Response response =
        authorRequests
            .deleteAuthor(INVALID_ID_DATA_TYPE)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();

    verifyError.verifyErrorInvalidIdType(response, INVALID_ID_DATA_TYPE);
  }
}
