package com.decade.practice.bdd.steps;

import com.decade.practice.bdd.context.AuthContext;
import com.decade.practice.bdd.context.UploadContext;
import com.decade.practice.resources.files.api.FileIntegrity;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RequiredArgsConstructor
public class UploadSteps {

      private final AuthContext authContext;
      private final UploadContext uploadContext;
      private final Environment environment;

      @Before
      public void setup() {
            RestAssured.port = Integer.parseInt(environment.getProperty("local.server.port"));
            RestAssured.baseURI = "http://localhost";
      }


      @When("user browse his file {string}")
      public void whenUpload(String filename) throws IOException {

            Response response = RestAssured.given()
                      .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
                      .queryParam("filename", filename)
                      .post("/files/upload")
                      .andReturn();
            response.then().statusCode(200)
                      .body("presignedUploadUrl", notNullValue())
                      .body("fileKey", notNullValue());

            String uploadUrl = response.jsonPath().getString("presignedUploadUrl");
            String key = response.jsonPath().getString("fileKey");

            response = RestAssured.given()
                      .urlEncodingEnabled(false)
                      .contentType(ContentType.BINARY)
                      .body(getClass().getResourceAsStream("/samples/" + filename))
                      .put(uploadUrl)
                      .andReturn();
            String eTag = response.header("ETag");

            uploadContext.finishStatus = response.statusCode();
            uploadContext.integrity = new FileIntegrity(key, eTag);
      }

      @Then("the file is saved")
      public void theDocumentIsSaved() {
            assertThat(uploadContext.integrity).isNotNull();
            assertThat(uploadContext.finishStatus).isEqualTo(200);
      }

}
