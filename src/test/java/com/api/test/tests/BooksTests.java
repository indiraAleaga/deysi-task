package com.api.test.tests;


import com.api.test.config.ConfigurationLoader;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BooksTests extends BaseApiTest {
    private final String BOOKS_URL = ConfigurationLoader.getProperty("booksUrl");

    @Test(description = "Get all books")
    public void testGetAllBooks() {
        Response response = given()
                .when()
                .get(BOOKS_URL)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        Assert.assertTrue(response.jsonPath().getList("id").size() > 0, "Books list should not be empty");
    }

    @Test(description = "Get a book by ID")
    public void testGetBookById() {
       int bookId = 1;
        String url = String.format("%s/%s", BOOKS_URL, bookId);
        Response response = given()
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getInt("id"), bookId, "Book ID should match");
    }
}

