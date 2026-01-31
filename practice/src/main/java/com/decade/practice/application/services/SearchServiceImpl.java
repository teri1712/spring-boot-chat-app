package com.decade.practice.application.services;

import com.decade.practice.api.dto.UserResponse;
import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.persistence.elastic.UserDocument;
import com.decade.practice.persistence.elastic.repositories.UserDocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

// TODO: Event driven
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserDocumentRepository userDocumentRepository;

    @Override
    public List<UserResponse> searchUsers(String query) {
        return userDocumentRepository.findByUsernameAndNameContaining(query, query)
                .stream().map(new Function<UserDocument, UserResponse>() {
                    @Override
                    public UserResponse apply(UserDocument userDocument) {
                        return UserResponse.builder()
                                .id(userDocument.getId())
                                .username(userDocument.getUsername())
                                .name(userDocument.getName())
                                .avatar(userDocument.getAvatar())
                                .gender(userDocument.getGender())
                                .build();
                    }
                }).toList();
    }

    @Override
    public List<UserResponse> searchMessages(String query) {
        return List.of();
    }
}
