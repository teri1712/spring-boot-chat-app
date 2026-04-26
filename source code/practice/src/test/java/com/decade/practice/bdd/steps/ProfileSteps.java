package com.decade.practice.bdd.steps;

import com.decade.practice.bdd.context.AuthContext;
import com.decade.practice.bdd.context.ProfileContext;
import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.users.dto.ProfileRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
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
    private final ProfileContext profileContext;
    private final Environment environment;

    @Before
    public void setup() {
        RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
        RestAssured.baseURI = "http://localhost";
    }

    Response passwordResponse;

    @When("changing password to {string} with submitted password {string}")
    public void changePassword(String newPassword, String submittedPassword) {
        passwordResponse = RestAssured

            .given()
            .contentType(ContentType.URLENC)
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .formParam("password", submittedPassword)
            .formParam("new_password", newPassword)

            .when()
            .post("/profiles/me/password");
    }

    @Then("password is changed successfully to {string}")
    public void changePasswordSuccess(String password) {
        profileContext.accessToken = passwordResponse.then()
            .statusCode(200)
            .extract()
            .body().jsonPath().getObject("accessToken", AccessToken.class);

        String username = authContext.profile.username();

        RestAssured.given()
            .contentType(ContentType.URLENC)
            .formParam("username", username)
            .formParam("password", password)

            .when()
            .post("/login")

            .then()
            .statusCode(200);
    }

    @Then("invalidate current session")
    public void invalidateCurrentSession() {
        RestAssured.given()
            .contentType(ContentType.URLENC)
            .formParam("refresh_token", authContext.accessToken.refreshToken())
            .post("/tokens/refresh")
            .then()
            .statusCode(401);
    }

    @Then("grant a new valid session")
    public void newSession() {
        RestAssured.given()
            .contentType(ContentType.URLENC)
            .formParam("refresh_token", profileContext.accessToken.refreshToken())
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

    private final LoginSteps loginSteps;

    @Given("user alr logins with username {string} and password {string}")
    public void userAlrLoginsWithUsernameAndPassword(String username, String password) {
        this.loginSteps.userExists(username, password);
        this.loginSteps.login(username, password);
    }
}
