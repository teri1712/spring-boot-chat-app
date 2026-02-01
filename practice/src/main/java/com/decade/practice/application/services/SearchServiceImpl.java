package com.decade.practice.application.services;

import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.application.usecases.SearchStore;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.SearchResultDto;
import com.decade.practice.dto.UserCreatedEvent;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.persistence.elastic.MessageDocument;
import com.decade.practice.persistence.elastic.UserDocument;
import com.decade.practice.persistence.elastic.repositories.MessageDocumentRepository;
import com.decade.practice.persistence.elastic.repositories.UserDocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

// TODO: Event driven
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService, SearchStore {

    private final UserDocumentRepository userDocumentRepository;
    private final MessageDocumentRepository messageDocumentRepository;

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
    public List<SearchResultDto> searchMessages(String query) {
        return List.of();
    }

    @Override
    public void save(UserCreatedEvent userCreatedEvent) {

        UserDocument document = new UserDocument();
        document.setAvatar(userCreatedEvent.getAvatar());
        document.setUsername(userCreatedEvent.getUsername());
        document.setName(userCreatedEvent.getName());
        document.setGender(userCreatedEvent.getGender());
        document.setDob(userCreatedEvent.getDob());
        document.setId(userCreatedEvent.getUserId());
        userDocumentRepository.save(document);

    }

    @Override
    public void save(EventDto eventDto) {
        if (eventDto.getTextEvent() != null) {
            MessageDocument document = new MessageDocument();
            document.setId(eventDto.getId());
            document.setSender(eventDto.getSender());
            document.setChatIdentifier(eventDto.getChat().getIdentifier());
            document.setPartnerName(eventDto.getPartner().getName());
            document.setContent(eventDto.getTextEvent().getContent());

            messageDocumentRepository.save(document);
        }
    }
}
