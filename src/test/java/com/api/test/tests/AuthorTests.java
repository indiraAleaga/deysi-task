package com.api.test.tests;


import com.api.test.config.ConfigurationLoader;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.restassured.AllureRestAssured;

import static io.restassured.RestAssured.given;

public class AuthorTests extends BaseApiTest {
    private final String AUTHORS_URL = ConfigurationLoader.getProperty("authorsUrl");


    @Test(description = "Get all authors")
    public void testGetAllAuthors() {
        Response response = given()
                .when()
                .get(AUTHORS_URL)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();

        Assert.assertTrue(response.jsonPath().getList("id").size() > 0, "Authors list should not be empty");
    }

    @Test(description = "Get an author by ID")
    public void testGetAuthorById() {
        int authorId = 1;
        String url = String.format("%s/%s", AUTHORS_URL, authorId);
        Response response = given()
                .filter(new AllureRestAssured())
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getInt("id"), authorId, "Author ID should match");
    }
}

