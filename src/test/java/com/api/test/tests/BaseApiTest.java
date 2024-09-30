package com.api.test.tests;

import com.api.test.config.ConfigurationLoader;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {

  @BeforeClass
  public void setup() {
    RestAssured.baseURI = ConfigurationLoader.getProperty("baseUrl");
  }
}
