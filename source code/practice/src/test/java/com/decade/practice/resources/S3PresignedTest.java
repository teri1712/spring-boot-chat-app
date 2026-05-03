package com.decade.practice.resources;

import com.decade.practice.BaseTestClass;
import com.decade.practice.resources.files.dto.S3PresignedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class S3PresignedTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private ObjectMapper objectMapper;

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
      @WithUserDetails("alice")
      void givenValidFilename_whenRequestPresignedUrlToUpload_thenReturnPresignedDetail() throws Exception {


            mockMvc.perform(post("/files/upload")
                                .queryParam("filename", "teri.txt"))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.filename").value("teri.txt"));
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
      @WithUserDetails("alice")
      void givenUploadedFile_whenSubmitImageEvent_thenReturnSuccess() throws Exception {


            MvcResult result = mockMvc.perform(post("/files/upload")
                                .queryParam("filename", "teri.txt"))
                      .andExpect(status().isOk())
                      .andReturn();

            S3PresignedResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), S3PresignedResponse.class);

            String eTag = Assertions.assertDoesNotThrow(() -> {
                  RestClient restClient = RestClient.builder()
                            .build();


                  return restClient.put()
                            .uri(URI.create(response.getPresignedUploadUrl()))
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body("NAB Innovation Center Vietnam")
                            .retrieve()
                            .toBodilessEntity()
                            .getHeaders().getETag();
            });

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                      
                          "file" : {
                                      "eTag": "%s",
                                      "fileKey": "%s"
                                    },
                          "width": 200,
                          "height": 200,
                          "filename": "teri.txt",
                          "format": "jpg"
                      }
                      """.formatted(eTag.replace("\"", "\\\""), response.getFileKey());

            mockMvc.perform(put("/chats/{chatIdentifier}/images/{postingId}", chatIdentifier, UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted());
      }

}
