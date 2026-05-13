package com.decade.practice.bdd.files;

import com.decade.practice.bdd.users.AuthContext;
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

    Response presignedResponse;
    Response response;

    @When("user browse his file {string}")
    public void whenUpload(String filename) {

        presignedResponse = RestAssured.given()
            .headers("Authorization", "Bearer " + authContext.accessToken.accessToken())
            .queryParam("filename", filename)

            .when()
            .post("/files/upload")

            .then().statusCode(200)
            .body("presignedUploadUrl", notNullValue())

            .and()
            .extract().response();


        String uploadUrl = response.jsonPath().getString("presignedUploadUrl");
        response = RestAssured.given()
            .urlEncodingEnabled(false)
            .contentType(ContentType.BINARY)
            .body(getClass().getResourceAsStream("/samples/" + filename))

            .when()
            .put(uploadUrl);

    }

    @Then("the file is saved")
    public void theFileIsSaved() {
        response.then().statusCode(200);
        String fileKey = presignedResponse.then()
            .body("fileKey", notNullValue())
            .and()
            .extract()
            .path("fileKey");
        String eTag = response.then()
            .headers("ETag", notNullValue())
            .and()
            .extract()
            .header("ETag");
        uploadContext.integrity = new FileIntegrity(fileKey, eTag);
    }

    @When("user alr uploaded the file {string}")
    public void userAlrUploadedTheFile(String filename) {
        this.whenUpload(filename);
        this.theFileIsSaved();
    }

}
