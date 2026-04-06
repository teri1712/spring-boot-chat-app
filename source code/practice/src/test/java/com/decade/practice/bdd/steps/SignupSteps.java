package com.decade.practice.bdd.steps;


import com.decade.practice.bdd.context.SignUpContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class SignupSteps {

      private final SignUpContext signUpContext;
      private final Environment environment;

      @Before
      public void setup() {
            RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
            RestAssured.baseURI = "http://localhost";
      }

      @When("user sign up new account with username {string} and password {string}")
      public void signUp(String username, String password) {
            Response response = RestAssured.given().contentType("application/json")
                      .body(
                                Map.of("username", username,
                                          "name", username,
                                          "dob", Instant.now(),
                                          "gender", 0.5,
                                          "password", password))
                      .post("/users");
            signUpContext.status = response.statusCode();
            signUpContext.username = username;
            signUpContext.password = password;
            if (response.statusCode() != 201) {
                  signUpContext.errorMessage = response.jsonPath().getString("detail");
            }
      }

      @Then("fails with error {string}")
      public void failsWithError(String error) {
            assertThat(signUpContext.errorMessage).isEqualTo(error);
      }


      @And("user set his avatar {string}")
      public void whenUpload(String fileName) throws IOException {


            Response response = RestAssured.given()
                      .auth().preemptive().basic(signUpContext.username, signUpContext.password)
                      .queryParam("filename", fileName)
                      .post("/files/upload")
                      .then().statusCode(200)
                      .extract().response();

            String uploadUrl = response.jsonPath().getString("presignedUploadUrl");
            String fileKey = response.jsonPath().getString("fileKey");

            response = RestAssured.given()
                      .urlEncodingEnabled(false)
                      .contentType(ContentType.BINARY)
                      .body(getClass().getResourceAsStream("/samples/" + fileName))
                      .put(uploadUrl)
                      .andReturn();
            String eTag = response.header("ETag");


            RestAssured.given()
                      .auth().preemptive().basic(signUpContext.username, signUpContext.password)
                      .contentType(ContentType.JSON)
                      .body("""
                                {
                                    "avatar" : {
                                                "eTag": "%s",
                                                "fileKey": "%s"
                                              }
                                }
                                """.formatted(eTag.replace("\"", "\\\""), fileKey))
                      .patch("/profiles/me")
                      .andReturn();
      }

      @Then("his profile is created successfully with the name {string} and avatar {string}")
      public void hisProfileIsCreatedSuccessfully(String name, String avatar) {
            assertThat(signUpContext.status).isEqualTo(201);
            Response response = RestAssured.given().auth().preemptive()
                      .basic(signUpContext.username, signUpContext.password)
                      .get("/profiles/me")
                      .then()
                      .statusCode(200)
                      .extract().response();
            String savedName = response.jsonPath().getString("name");
            String savedAvatar = response.jsonPath().getString("avatar");
            assertThat(savedName).isEqualTo(name);
            assertThat(savedAvatar).contains(avatar);
      }
}
