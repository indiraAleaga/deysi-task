package com.api.test.verifications;

import static com.api.test.constants.ApiTestsConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import io.restassured.response.Response;
import org.testng.Assert;

public class ErrorMessagesVerification {

  public void verifyErrorInvalidPageCount(Response response) {
    assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
    assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
    Assert.assertTrue(
        response
            .jsonPath()
            .getMap(ERROR_MESSAGE_ERRORS_KEY)
            .containsKey(ERROR_MESSAGE_PAGE_COUNT_KEY),
        "Error key is missing");
    String errorMessage =
        response
            .jsonPath()
            .getString(
                String.format(
                    "%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_PAGE_COUNT_KEY));
    Assert.assertTrue(
        errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_SYSTEM32),
        "Expected error message is missing or incorrect");
  }

  public void verifyErrorInvalidDateFormat(Response response) {
    assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
    assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
    Assert.assertTrue(
        response
            .jsonPath()
            .getMap(ERROR_MESSAGE_ERRORS_KEY)
            .containsKey(ERROR_MESSAGE_PUBLISH_DATE_KEY),
        "Error key is missing");
    String errorMessage =
        response
            .jsonPath()
            .getString(
                String.format(
                    "%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_PUBLISH_DATE_KEY));
    Assert.assertTrue(
        errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_DATE),
        "Expected error message is missing or incorrect");
  }

  public void verifyErrorInvalidIdBookType(Response response) {
    assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
    assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
    Assert.assertTrue(
        response.jsonPath().getMap(ERROR_MESSAGE_ERRORS_KEY).containsKey(ERROR_MESSAGE_ID_BOOK_KEY),
        "Error key is missing");
    String errorMessage =
        response
            .jsonPath()
            .getString(
                String.format("%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_ID_BOOK_KEY));
    Assert.assertTrue(
        errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_SYSTEM32),
        "Expected error message is missing or incorrect");
  }

  public void verifyErrorInvalidIdType(Response response, String value) {
    assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
    assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
    Assert.assertTrue(
        response.jsonPath().getMap(ERROR_MESSAGE_ERRORS_KEY).containsKey(ERROR_MESSAGE_ID_KEY),
        "Error key is missing");
    String errorMessage =
        response
            .jsonPath()
            .getString(String.format("%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_ID_KEY));
    Assert.assertTrue(
        errorMessage.contains(String.format(ERROR_MESSAGE_NOT_VALID_VALUE, value)),
        "Expected error message is missing or incorrect");
  }

  public void verifyErrorNotFound(Response response) {
    assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_NOT_FOUND);
    assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
  }
}
