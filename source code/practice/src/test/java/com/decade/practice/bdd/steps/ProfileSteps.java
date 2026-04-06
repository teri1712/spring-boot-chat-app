package com.decade.practice.bdd.steps;

import com.decade.practice.bdd.context.AuthContext;
import com.decade.practice.bdd.context.ChangePasswordContext;
import com.decade.practice.bdd.context.UploadContext;
import com.decade.practice.users.dto.ProfileRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ProfileSteps {
      private final AuthContext authContext;
      private final UploadContext uploadContext;
      private final Environment environment;

      private final ChangePasswordContext changePasswordContext;

      private int changeAvatarCode;

      @Before
      public void setup() {
            RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
            RestAssured.baseURI = "http://localhost";
      }


      @When("changing password to {string} with submitted password {string}")
      public void changePassword(String newPassword, String submittedPassword) {

            this.changePasswordContext.oldToken = authContext.accessToken;
            Response response = RestAssured.given().contentType(ContentType.URLENC)
                      .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
                      .formParam("password", submittedPassword)
                      .formParam("new_password", newPassword)
                      .post("/profiles/me/password");
            this.changePasswordContext.oldToken = authContext.accessToken;
            changePasswordContext.status = response.statusCode();
      }

      @Then("password is changed successfully to {string}")
      public void changePasswordSuccess(String password) {
            assertThat(changePasswordContext.status).isEqualTo(200);
            String username = authContext.profile.username();
            RestAssured.given().contentType(ContentType.URLENC)
                      .formParam("username", username)
                      .formParam("password", password)
                      .post("/login")
                      .then()
                      .statusCode(200);
      }

      @Then("invalidate old session")
      public void invalidateCurrentSession() {
            RestAssured.given()
                      .contentType(ContentType.URLENC)
                      .formParam("refresh_token", changePasswordContext.oldToken.refreshToken())
                      .post("/tokens/refresh")
                      .then()
                      .statusCode(401);
      }

      @Then("grant a new valid session")
      public void newSession() {
            RestAssured.given()
                      .contentType(ContentType.URLENC)
                      .formParam("refresh_token", authContext.accessToken.refreshToken())
                      .post("/tokens/refresh")
                      .then()
                      .statusCode(200);
      }

      @When("the user update his name to {string} and his gender to {double}")
      public void theUserUpdateHisNameToAndHisGenderTo(String name, double gender) {
            ProfileRequest request = new ProfileRequest();
            request.setName(name);
            request.setGender((float) gender);
            RestAssured.given().contentType(ContentType.JSON)
                      .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
                      .body(request)
                      .patch("/profiles/me")
                      .then().statusCode(200);
      }

      @Then("his profile is reflected correctly with the name {string} and gender {string}")
      public void hisProfileIsReflectedCorrectlyWithTheNameAndGender(String name, String gender) {
            Response response = RestAssured.given()
                      .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
                      .get("/profiles/me");
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("name")).isEqualTo(name);
            assertThat(response.jsonPath().getString("gender")).isEqualTo(gender);
      }


      @When("set as his avatar")
      public void changeHisAvatar() {
            String eTag = uploadContext.integrity.eTag();
            String fileKey = uploadContext.integrity.fileKey();
            changeAvatarCode = RestAssured.given().headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
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
                      .then().extract().statusCode();
      }


      @And("his profile avatar is reflected to the file {string}")
      public void hisProfileAvatarIsReflectedToTheNewAvatar(String filename) {
            Response response = RestAssured.given()
                      .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
                      .get("/profiles/me")
                      .andReturn();

            assertThat(changeAvatarCode).isEqualTo(200);
            assertThat(response.jsonPath().getString("avatar")).isNotNull();
            assertThat(response.jsonPath().getString("avatar")).contains(filename);
            assertThat(response.statusCode()).isEqualTo(200);
      }
}
