package com.decade.practice.bdd.users;


import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@RequiredArgsConstructor
public class SignupSteps {

    private final Environment environment;

    @Before
    public void setup() {
        RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
        RestAssured.baseURI = "http://localhost";
    }

    Response response;

    @When("user sign up new account with username {string} and password {string}")
    public void signUp(String username, String password) {
        response = RestAssured
            .given()
            .contentType("application/json")
            .body(
                Map.of("username", username,
                    "name", username,
                    "dob", Instant.now(),
                    "gender", 0.5,
                    "password", password))


            .when()
            .post("/users");

    }

    @Then("fails with error {string}")
    public void failsWithError(String error) {
        response
            .then()
            .statusCode(400)
            .body("detail", equalTo(error))
        ;
    }
}
