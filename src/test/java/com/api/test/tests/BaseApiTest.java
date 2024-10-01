package com.api.test.tests;

import com.api.test.config.ConfigurationLoader;
import com.api.test.verification.ErrorMessagesVerification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
  protected Gson gson;

  protected ErrorMessagesVerification verifyError;

  @BeforeClass
  public void setup() {

    RestAssured.baseURI = ConfigurationLoader.getProperty("baseUrl");
    gson = new GsonBuilder().setPrettyPrinting().create();
    verifyError = new ErrorMessagesVerification();
  }
}
