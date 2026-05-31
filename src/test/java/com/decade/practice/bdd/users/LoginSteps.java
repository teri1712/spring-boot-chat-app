package com.decade.practice.bdd.users;

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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

    Response loginResponse;

    @When("user logins with username {string} and password {string}")
    public void whenLogin(String username, String password) {
        loginResponse = RestAssured
            .given()
            .contentType(ContentType.URLENC)
            .formParam("username", username)
            .formParam("password", password)

            .when()
            .post("/login");
    }

    @When("user alr logins with username {string} and password {string}")
    public void alrLogin(String username, String password) {
        this.givenExist(username, password);
        this.whenLogin(username, password);
        this.thenSuccess();
    }

    @Then("the user should be granted access and their profile information")
    public void thenSuccess() {
        loginResponse.then()
            .statusCode(200)
            .body("accessToken", notNullValue())
            .body("profile", notNullValue());

        authContext.accessToken = loginResponse.jsonPath().getObject("accessToken", AccessToken.class);
        authContext.profile = loginResponse.jsonPath().getObject("profile", ProfileResponse.class);

    }

    @Then("the user should be denied access with {string} message")
    public void thenDenied(String message) {
        loginResponse.then()
            .statusCode(401)
            .body("detail", equalTo(message));
    }

    @Given("username {string} does not exist")
    public void givenDoesNotExist(String username) {
    }

    @Given("user alr exist with username {string} and password {string}")
    public void givenExist(String username, String password) {
        RestAssured.given()
            .contentType("application/json")
            .body(
                Map.of("username", username.replace(" ", ""),
                    "name", username,
                    "dob", Instant.now(),
                    "gender", 0.5,
                    "password", password))
            .post("/users")
            .then()
            .statusCode(201);
    }

}
