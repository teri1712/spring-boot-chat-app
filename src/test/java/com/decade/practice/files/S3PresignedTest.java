package com.decade.practice.files;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.S3Dataset;
import com.decade.practice.files.api.DownloadPathGenerator;
import com.decade.practice.files.api.FileIntegrity;
import com.decade.practice.files.dto.PresignedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@ComponentTest(datasets = S3Dataset.class)
class S3PresignedTest {
    final MockMvc mockMvc;
    final ObjectMapper objectMapper;
    final DownloadPathGenerator generator;

    @Test
    @WithMockUser(username = "alice")
    void givenValidFilename_whenRequestPresignedUrlToUpload_thenReturnPresignedDetail() throws Exception {

        mockMvc.perform(post("/files/upload")
                .queryParam("filename", "teri.txt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("teri.txt"));
    }

    @Test
    @WithMockUser(username = "alice")
    void givenUploadedFile_whenSubmitImageEvent_thenReturnSuccess() throws Exception {


        MvcResult result = mockMvc.perform(post("/files/upload")
                .queryParam("filename", "teri.txt"))
            .andExpect(status().isOk())
            .andReturn();

        PresignedResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), PresignedResponse.class);

        String eTag = assertDoesNotThrow(() -> {
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

        assertDoesNotThrow(() -> {
            generator.generateDownload(new FileIntegrity(response.getFileKey(), eTag));
        });
    }

}
