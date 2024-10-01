package com.api.test.tests;

import static com.api.test.constants.ApiTestsConstants.INVALID_ID_DATA_TYPE;
import static com.api.test.constants.ApiTestsConstants.NON_EXISTENT_ID;
import static org.testng.Assert.*;

import com.api.test.data_providers.DataProviderClass;
import com.api.test.models.Book;
import com.api.test.repositories.BookRepository;
import com.api.test.requests.BookRequests;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Date;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BooksTests extends BaseApiTest {

  private BookRequests bookRequests;
  private BookRepository bookRepository;
  private Book expectedBook;

  @BeforeClass
  public void beforeClass() {

    bookRepository = new BookRepository();
    bookRepository.loadBooks("books.json");
    expectedBook = bookRepository.getAllBooks().get(0);
    bookRequests = new BookRequests();
  }

  @Test(description = "Get all books")
  public void testGetAllBooks() {
    Response response =
        bookRequests.getAllBooks().then().statusCode(HttpStatus.SC_OK).extract().response();
    assertFalse(response.jsonPath().getList("id").isEmpty(), "Books list should not be empty");

    int totalBooks = response.jsonPath().getList("id").size();
    assertEquals(totalBooks, 200, "Total number of books should be 200");
  }

  @Test(description = "Get a book by ID")
  public void testGetBookById() {
    Book responseBook =
        bookRequests
            .getBookById(expectedBook.getId())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response()
            .as(Book.class);
    assertNotNull(responseBook, "Book not found in repository");
    assertEquals(
        responseBook.getId(),
        expectedBook.getId(),
        String.format(
            "The expected book id is [%s], but we got [%s]",
            expectedBook.getId(), responseBook.getId()));
    assertEquals(
        responseBook.getTitle(),
        expectedBook.getTitle(),
        String.format(
            "The expected title id is [%s], but we got [%s]",
            expectedBook.getTitle(), responseBook.getTitle()));
    assertFalse(responseBook.getDescription().isEmpty(), "Book description should not be empty");
    assertEquals(
        responseBook.getPageCount(),
        expectedBook.getPageCount(),
        String.format(
            "The expected page count id is [%s], but we got [%s]",
            expectedBook.getPageCount(), responseBook.getPageCount()));
    assertFalse(responseBook.getExcerpt().isEmpty(), "Excerpt should not be empty");
    assertFalse(responseBook.getPublishDate().isEmpty(), "Publish date should not be empty");
  }

  @Test(description = "Get a book by NON-Existent ID")
  public void testGetBookByIdNotFound() {
    Response response =
        bookRequests
            .getBookById(NON_EXISTENT_ID)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Get a book by invalid ID data ype")
  public void testGetBookByInvalidIdDataType() {
    Response response =
        bookRequests
            .getBookById(INVALID_ID_DATA_TYPE)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();
    verifyError.verifyErrorInvalidIdType(response, INVALID_ID_DATA_TYPE);
  }

  @Test(description = "Create a Book with valid data")
  public void testCreateBookValid() {
    Book newBook = bookRepository.getFakeNewBook();
    String newBookJson = gson.toJson(newBook);
    Response response = bookRequests.createBook(newBookJson).then().extract().response();

    assertEquals(response.statusCode(), HttpStatus.SC_OK); // HTTP 201 Created
    assertNotNull(response.jsonPath().getString("id"), "The bookId should not be empty");

    Book createdBook = response.as(Book.class);
    assertNotNull(createdBook, "Book not found in repository");
    assertNotNull(createdBook.getId(), "The book id should be generate");
    assertEquals(
        createdBook.getTitle(),
        newBook.getTitle(),
        String.format(
            "The expected title id is [%s], but we got [%s]",
            newBook.getTitle(), createdBook.getTitle()));
    assertFalse(createdBook.getDescription().isEmpty(), "Book description should not be empty");
    assertEquals(
        createdBook.getPageCount(),
        newBook.getPageCount(),
        String.format(
            "The expected page count id is [%s], but we got [%s]",
            newBook.getPageCount(), createdBook.getPageCount()));
    assertFalse(createdBook.getExcerpt().isEmpty(), "Excerpt should not be empty");
    assertFalse(createdBook.getPublishDate().isEmpty(), "Publish date should not be empty");
  }

  @Test(description = "Create a book with duplicated ID")
  public void testCreateBookDuplicateId() {
    // Assuming we can only create books with a unique ID
    int existentID = (Integer) expectedBook.getId();
    Book newBook = bookRepository.getFakeNewBook();
    newBook.setId(existentID);
    String newBookJson = gson.toJson(newBook);
    Response response = bookRequests.createBook(newBookJson).then().extract().response();

    assertEquals(
        response.statusCode(),
        HttpStatus.SC_BAD_REQUEST,
        String.format(
            "Expected %s status but got %s", HttpStatus.SC_BAD_REQUEST, response.statusCode()));
  }

  @Test(description = "Create a book invalid date format")
  public void testCreateWithInvalidDateFormat() {
    Book newBook = bookRepository.getFakeNewBook();
    newBook.setPublishDate(new Date().toString());
    String newBookJson = gson.toJson(newBook);
    Response response =
        bookRequests
            .createBook(newBookJson)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST) // Expecting HTTP 400 Bad Request
            .contentType(ContentType.JSON)
            .extract()
            .response();

    verifyError.verifyErrorInvalidDateFormat(response);
  }

  @Test(
      description = "Create a Book with different values for page count",
      dataProvider = "pageCountProvider",
      dataProviderClass = DataProviderClass.class)
  public void testCreateBookWithPageCount(
      Object pageCount, int expectedStatus, String description) {
    Book newBook = bookRepository.getFakeNewBook();
    newBook.setPageCount(pageCount);
    String newBookJson = gson.toJson(newBook);
    Response response = bookRequests.createBook(newBookJson).then().extract().response();

    assertEquals(
        response.statusCode(),
        expectedStatus,
        String.format(
            "Failed: Expected status %d, but got %d. Description: %s",
            expectedStatus, response.getStatusCode(), description));

    if (expectedStatus == HttpStatus.SC_BAD_REQUEST) {
      verifyError.verifyErrorInvalidPageCount(response);

    } else {
      assertEquals(response.jsonPath().getInt("pageCount"), (Integer) pageCount);
    }
  }

  @Test(description = "Update Book with valid data")
  public void testUpdateBook() {
    Book newBook = bookRepository.getFakeNewBook();
    newBook.setId(expectedBook.getId());
    String newBookJson = gson.toJson(newBook);
    Book updatedBook =
        bookRequests
            .updateBook(newBook.getId(), newBookJson)
            .then()
            .extract()
            .response()
            .as(Book.class);

    assertNotNull(updatedBook, "Book not found in repository");
    assertEquals(
        updatedBook.getId(),
        newBook.getId(),
        String.format(
            "The expected book id is [%s], but we got [%s]", newBook.getId(), updatedBook.getId()));
    assertEquals(
        updatedBook.getTitle(),
        newBook.getTitle(),
        String.format(
            "The expected title id is [%s], but we got [%s]",
            newBook.getTitle(), updatedBook.getTitle()));
    assertFalse(updatedBook.getDescription().isEmpty(), "Book description should not be empty");
    assertEquals(
        updatedBook.getPageCount(),
        newBook.getPageCount(),
        String.format(
            "The expected page count id is [%s], but we got [%s]",
            newBook.getPageCount(), updatedBook.getPageCount()));
    assertFalse(updatedBook.getExcerpt().isEmpty(), "Excerpt should not be empty");
    assertFalse(updatedBook.getPublishDate().isEmpty(), "Publish date should not be empty");
  }

  @Test(description = "Update Book with NON-Existent ID")
  public void testUpdateBookNonExistentID() {
    Book newBook = bookRepository.getFakeNewBook();
    String newBookJson = gson.toJson(newBook);
    Response response =
        bookRequests
            .updateBook(NON_EXISTENT_ID, newBookJson)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Update Book with invalid Data type ID")
  public void testUpdateBookInvalidDataTypeID() {
    Book newBook = bookRepository.getFakeNewBook();
    String newBookJson = gson.toJson(newBook);
    Response response =
        bookRequests
            .updateBook(INVALID_ID_DATA_TYPE, newBookJson)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();
    verifyError.verifyErrorInvalidIdType(response, INVALID_ID_DATA_TYPE);
  }

  @Test(description = "Update Book with mismatching ID")
  public void testUpdateBookWithMisMatchingID() {
    int idMismatch = (Integer) expectedBook.getId() + 1;
    Book newBook = bookRepository.getFakeNewBook();
    newBook.setId(expectedBook.getId());
    String newBookJson = gson.toJson(newBook);

    bookRequests
        .updateBook(idMismatch, newBookJson)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .extract()
        .response();
  }

  @Test(
      description = "Update Book with different page count values",
      dataProvider = "pageCountProvider",
      dataProviderClass = DataProviderClass.class)
  public void testUpdateBookWithPageCount(
      Object pageCount, int expectedStatus, String description) {
    Book book = bookRepository.getFakeNewBook();
    book.setId(expectedBook.getId());
    book.setPageCount(pageCount);
    String newBookJson = gson.toJson(book);
    Response response =
        bookRequests.updateBook(book.getId(), newBookJson).then().extract().response();

    assertEquals(
        response.statusCode(),
        expectedStatus,
        " Failed: Expected status "
            + expectedStatus
            + ", but got "
            + response.getStatusCode()
            + ". Description: "
            + description);
    if (expectedStatus == HttpStatus.SC_BAD_REQUEST) {
      verifyError.verifyErrorInvalidPageCount(response);
    } else {
      assertEquals(response.jsonPath().getInt("pageCount"), (Integer) pageCount);
    }
  }

  @Test(description = "Delete book by ID")
  public void testDeleteBookByID() {
    bookRequests.deleteBook(expectedBook.getId()).then().statusCode(HttpStatus.SC_OK);
    bookRequests.getBookById(expectedBook.getId()).then().statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test(description = "Delete Book with NON-Existent ID")
  public void testDeleteBookNonExistentID() {
    Response response =
        bookRequests
            .deleteBook(NON_EXISTENT_ID)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .response();
    verifyError.verifyErrorNotFound(response);
  }

  @Test(description = "Delete Book with invalid Data type ID")
  public void testDeleteBookInvalidDataTypeID() {
    Response response =
        bookRequests
            .deleteBook(INVALID_ID_DATA_TYPE)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();
    verifyError.verifyErrorInvalidIdType(response, INVALID_ID_DATA_TYPE);
  }
}
