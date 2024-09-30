package com.api.test.tests;


import com.api.test.models.Book;
import com.api.test.data_provider.DataProviderClass;
import com.api.test.repository.BooksRepository;
import com.api.test.requests.BooksRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Random;

public class BooksTests extends BaseApiTest {

    private BooksRequests booksRequests;

    private BooksRepository bookRepository;

    private Book expectedBook;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeClass
    public void beforeClass() {

        bookRepository = new BooksRepository();
        bookRepository.loadBooks("books.json");
        expectedBook = bookRepository.getAllBooks().get(0);
        booksRequests = new BooksRequests();

    }

    @Test(description = "Get all books")
    public void testGetAllBooks() {
        Response response = booksRequests.getAllBooks()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        Assert.assertFalse(response.jsonPath().getList("id").isEmpty(), "Books list should not be empty");
    }

    @Test(description = "Get a book by ID")
    public void testGetBookById() {
        Book responseBook = booksRequests.getBookById((Integer)expectedBook.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response().as(Book.class);
        Assert.assertNotNull(expectedBook, "Book not found in repository");
        Assert.assertEquals(responseBook.getId(), expectedBook.getId());
        Assert.assertEquals(responseBook.getTitle(), expectedBook.getTitle());
        Assert.assertFalse(responseBook.getDescription().isEmpty());
        Assert.assertEquals(responseBook.getPageCount(), expectedBook.getPageCount());
        Assert.assertFalse(responseBook.getExcerpt().isEmpty());
        Assert.assertFalse(responseBook.getPublishDate().isEmpty());
    }

    @Test(description = "Get a book by ID not found")
    public void testGetBookByIdNotFound() {
        Random random = new Random();
        int bookId = random.nextInt(10000);
        booksRequests.getBookById(bookId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);

    }

    @Test(description = "Create a valid book")
    public void testCreateBookValid() {
        Book newBook = bookRepository.getFakeNewBook();
        String newBookJson = gson.toJson(newBook);
        Response response=  booksRequests.createBook(newBookJson)
                .then()
                .extract().response();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK); // HTTP 201 Created
        Assert.assertNotNull(response.jsonPath().getString("id"));
        Book createdBook = response.as(Book.class);
        Book responseBook = booksRequests.getBookById((Integer)createdBook.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response().as(Book.class);
        Assert.assertNotNull(responseBook, "Book not found in repository");
        Assert.assertEquals(responseBook.getId(), createdBook.getId());


    }

    @Test(description = "Create a book with duplicated ID")
    public void testCreateBookDuplicateId() {
        // Assuming we can only create books with a unique ID
        int existentID = (Integer)expectedBook.getId();
        Book newBook = bookRepository.getFakeNewBook();
        newBook.setId(existentID);
        String newBookJson = gson.toJson(newBook);
        Response response=  booksRequests.createBook(newBookJson)
                .then()
                .extract().response();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "Create a book invalid date format")
    public void testCreateWithInvalidDateFormat() {
        Book newBook = bookRepository.getFakeNewBook();
        newBook.setPublishDate(new Date().toString());
        String newBookJson = gson.toJson(newBook);
        Response response = booksRequests.createBook(newBookJson)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST) // Expecting HTTP 400 Bad Request
                .contentType(ContentType.JSON)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getString("title"),"One or more validation errors occurred." );
        Assert.assertNotNull(response.jsonPath().get("traceId"));
        Assert.assertTrue(response.jsonPath().getMap("errors").containsKey("$.publishDate"), "Error key for pageCount is missing");
        String errorMessage = response.jsonPath().getString("errors.'$.publishDate'[0]");
        Assert.assertTrue(errorMessage.contains("The JSON value could not be converted to System.DateTime"),
                "Expected error message is missing or incorrect");

    }

    @Test(dataProvider = "pageCountProvider", dataProviderClass = DataProviderClass.class)
    public void testCreateBookWithPageCount( Object pageCount, int expectedStatus, String description){
        Book newBook = bookRepository.getFakeNewBook();
        newBook.setPageCount(pageCount);
        String newBookJson = gson.toJson(newBook);
        Response response=  booksRequests.createBook(newBookJson)
                .then()
                .extract().response();

        Assert.assertEquals(response.statusCode(), expectedStatus, " Failed: Expected status " + expectedStatus + ", but got " + response.getStatusCode() + ". Description: " + description);
        if (expectedStatus == HttpStatus.SC_BAD_REQUEST){
            Assert.assertEquals(response.jsonPath().getString("title"),"One or more validation errors occurred." );
            Assert.assertNotNull(response.jsonPath().get("traceId"));
            Assert.assertTrue(response.jsonPath().getMap("errors").containsKey("$.pageCount"), "Error key for pageCount is missing");
            String errorMessage = response.jsonPath().getString("errors.'$.pageCount'[0]");
            Assert.assertTrue(errorMessage.contains("The JSON value could not be converted to System.Int32"),
                    "Expected error message is missing or incorrect");
        }else {
            Assert.assertEquals(response.jsonPath().getInt("pageCount"),(Integer)pageCount );
        }
    }

    @Test(description = "Delete book")
    public void testDeleteBookByID() {
        booksRequests.deleteBook((Integer)expectedBook.getId())
                .then()
                .statusCode(HttpStatus.SC_OK);
       booksRequests.getBookById((Integer)expectedBook.getId())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);


    }

    @Test(dataProvider = "pageCountProvider", dataProviderClass = DataProviderClass.class)
    public void testUpdateBookWithPageCount( Object pageCount, int expectedStatus, String description){
        //Book newBook = bookRepository.getFakeNewBook();
        expectedBook.setPageCount(pageCount);
        String newBookJson = gson.toJson(expectedBook);
        Response response=  booksRequests.updateBook((Integer) expectedBook.getId(), newBookJson)
                .then()
                .extract().response();

        Assert.assertEquals(response.statusCode(), expectedStatus, " Failed: Expected status " + expectedStatus + ", but got " + response.getStatusCode() + ". Description: " + description);
        if (expectedStatus == HttpStatus.SC_BAD_REQUEST){
            Assert.assertEquals(response.jsonPath().getString("title"),"One or more validation errors occurred." );
            Assert.assertNotNull(response.jsonPath().get("traceId"));
            Assert.assertTrue(response.jsonPath().getMap("errors").containsKey("$.pageCount"), "Error key for pageCount is missing");
            String errorMessage = response.jsonPath().getString("errors.'$.pageCount'[0]");
            Assert.assertTrue(errorMessage.contains("The JSON value could not be converted to System.Int32"),
                    "Expected error message is missing or incorrect");
        }
        else {
            Assert.assertEquals(response.jsonPath().getInt("pageCount"),(Integer)pageCount );
        }
    }
}

