package com.decade.practice.bdd.chats;

import com.decade.practice.bdd.users.AuthContext;
import com.decade.practice.bdd.users.LoginSteps;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ConversationSteps {

    private final AuthContext authContext;
    private final ConversationContext conversationContext;
    private final LoginSteps loginSteps;
    private final Environment environment;

    @Before
    public void setup() {
        RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
        RestAssured.baseURI = "http://localhost";
    }

    @Given("there alr is a conversation from {string} to  {string}")
    public void conversationBetweenUserAndExists(String user1, String user2) {
        loginSteps.alrLogin(user2, "password123");
        UUID partnerId = authContext.profile.id();

        loginSteps.alrLogin(user1, "password123");

        conversationContext.me = user1;
        conversationContext.partner = user2;
        conversationContext.chatId = RestAssured.given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())

            .when()
            .put("/direct-chats/{partnerId}", partnerId)

            .then()
            .statusCode(201)

            .extract().path("mapping.chatId");
    }


    @And("user alr send {string}")
    public void userAlrSend(String text) {
        RestAssured.given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .contentType("application/json")
            .body(Map.of("content", text))

            .put("/chats/{chatId}/texts/{postingId}", conversationContext.chatId, UUID.randomUUID())
            .then()
            .statusCode(202);
    }
}
