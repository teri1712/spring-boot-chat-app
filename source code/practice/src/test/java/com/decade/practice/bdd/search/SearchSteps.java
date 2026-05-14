package com.decade.practice.bdd.search;

import com.decade.practice.bdd.chats.ConversationContext;
import com.decade.practice.bdd.users.AuthContext;
import com.decade.practice.bdd.users.LoginSteps;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import static org.hamcrest.Matchers.equalTo;

@RequiredArgsConstructor
public class SearchSteps {

    private final AuthContext authContext;
    private final Environment environment;
    private final LoginSteps loginSteps;
    private final ConversationContext conversationContext;

    @Before
    public void setup() {
        RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
        RestAssured.baseURI = "http://localhost";
    }

    @Given("user {string} exists")
    public void userExists(String username) {
        loginSteps.givenExist(username, "password123");
    }

    @And("user {string} alr logins")
    public void userAlrLogins(String username) {
        loginSteps.alrLogin(username, "password123");
    }


    Response searchResponse;

    @When("{string} search user for word {string}")
    public void searchUserForWord(String username, String query) {
        searchResponse = RestAssured.given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .queryParam("query", query)

            .when()
            .get("/people");
    }

    @Then("the user {string} must be returned")
    public void theUserMustBeReturned(String user) {
        searchResponse
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo(user));
    }

    @Given("user {string} not exists")
    public void userNotExists(String user) {
    }

    @Then("the user {string} must not be returned")
    public void theUserMustNotBeReturned(String user) {
        searchResponse
            .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }


    @When("user find {string} in the conversation")
    public void userFindInTheConversation(String query) {
        searchResponse = RestAssured
            .given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .queryParam("query", query)

            .when()
            .get("/chat-histories/{chatId}", conversationContext.chatId);
    }

    @Then("message {string} must be returned")
    public void messageMustBeReturned(String query) {
        searchResponse
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].content", equalTo(query));
    }

    @And("no {string} messages in the conversation")
    public void noMessagesInTheConversation(String message) {
    }

    @Then("no matching messages returned")
    public void noMatchingMessagesReturned() {
        searchResponse
            .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @When("user {string} search message of that conversation")
    public void userSearchMessageOfThatConversation(String user) {
        loginSteps.alrLogin(user, "password123");


        searchResponse = RestAssured.given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .queryParam("query", "vcl")

            .when()
            .get("/chat-histories/{chatId}", conversationContext.chatId);
    }

    @Then("server reject that")
    public void serverRejectThat() {
        searchResponse
            .then()
            .statusCode(403);
    }
}
