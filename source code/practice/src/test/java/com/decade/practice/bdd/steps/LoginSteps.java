package com.decade.practice.bdd.steps;

import com.decade.practice.bdd.context.AuthContext;
import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.users.dto.ProfileResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@RequiredArgsConstructor
public class LoginSteps {

      private final AuthContext authContext;
      private final Environment environment;

      @Before
      public void setup() {
            RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
            RestAssured.baseURI = "http://localhost";
      }

      @When("user logins with username {string} and password {string}")
      public void login(String username, String password) {
            Response response = RestAssured.given().contentType(ContentType.URLENC)
                      .formParam("username", username)
                      .formParam("password", password)
                      .post("/login");
            authContext.statusCode = response.statusCode();
            authContext.accessToken = response.jsonPath().getObject("accessToken", AccessToken.class);
            authContext.profile = response.jsonPath().getObject("profile", ProfileResponse.class);
            if (response.statusCode() != 200) {
                  authContext.errorMessage = response.jsonPath().getString("detail");
            }
      }

      @Then("the user should be granted access and their profile information")
      public void thenSuccess() {
            assertThat(authContext.statusCode).isEqualTo(200);
            assertThat(authContext.accessToken).isNotNull();
            assertThat(authContext.profile).isNotNull();
      }

      @Then("the user should be denied access with {string} message")
      public void thenDenied(String message) {
            assertThat(authContext.statusCode).isEqualTo(401);
            assertThat(authContext.errorMessage).isEqualTo(message);
      }

      @Given("username {string} does not exist")
      public void usernameDoesNotExist(String username) {
      }

      @Given("user exist with username {string} and password {string}")
      public void userExists(String username, String password) {
            RestAssured.given().contentType("application/json")
                      .body(
                                Map.of("username", username,
                                          "name", username,
                                          "dob", Instant.now(),
                                          "gender", 0.5,
                                          "password", password))
                      .post("/users")
                      .then()
                      .statusCode(201)
                      .body("username", equalTo(username));
      }

}
