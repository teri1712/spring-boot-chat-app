package com.decade.practice.web.rest;

import com.decade.practice.common.BaseTestClass;
import com.decade.practice.persistence.elastic.MessageDocument;
import com.decade.practice.persistence.elastic.UserDocument;
import com.decade.practice.persistence.elastic.repositories.MessageDocumentRepository;
import com.decade.practice.persistence.elastic.repositories.UserDocumentRepository;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SearchControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private MessageDocumentRepository messageDocumentRepository;

    @Test
    void givenUsersExist_whenFindUsers_shouldReturnUserList() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument();
        user.setId(userId);
        user.setUsername("searchable_user");
        user.setName("Searchable Name");
        user.setGender(User.FEMALE);
        user.setAvatar(new ImageSpec());
        userDocumentRepository.save(user);

        mockMvc.perform(get("/users")
                        .param("query", "searchable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("searchable_user"))
                .andExpect(jsonPath("$[0].name").value("Searchable Name"));
    }

    @Test
    void givenUnauthenticatedUser_whenFindMessages_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/me/history/messages")
                        .param("query", "hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("alice")
    void givenAuthenticatedUser_whenFindMessages_thenReturnsMessageHistory() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID partnerId = UUID.randomUUID();
        MessageDocument message = new MessageDocument();
        message.setId(UUID.randomUUID());
        message.setOwner(userId);
        message.setContent("unique message content");
        message.setPartnerName("Partner");
        message.setChatIdentifier(new ChatIdentifier(userId, partnerId));
        messageDocumentRepository.save(message);

        mockMvc.perform(get("/me/history/messages")
                        .param("query", "unique")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("unique message content"))
                .andExpect(jsonPath("$[0].partnerName").value("Partner"));
    }

    @Test
    void givenEmptyQuery_whenFindUsers_thenReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/users")
                        .param("query", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
