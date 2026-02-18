package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.exceptions.ChatIdentifierUniqueException;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.services.ChatPolicyService;
import com.decade.practice.engagement.domain.services.GroupChatFactory;
import com.decade.practice.engagement.domain.services.PrivateChatFactory;
import com.decade.practice.engagement.dto.ChatResponse;
import com.decade.practice.engagement.dto.mapper.ChatMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService {

    private final ParticipantRepository participants;
    private final ChatRepository chats;
    private final GroupChatFactory groupChatFactory;
    private final PrivateChatFactory privateChatFactory;
    private final ChatPolicyService chatPolicyService;
    private final ChatMapper chatMapper;

    @PersistenceContext
    private EntityManager em;

    @Override
    public ChatResponse create(CreateGroupChatCommand command) {
        ChatCreators creators = new ChatCreators(command.callerId(), command.partnerId());
        Chat chat = groupChatFactory.create(creators, 1000, command.roomName());
        chats.save(chat);
        Participant caller = new Participant(command.callerId(), chat.getIdentifier());
        Participant partner = new Participant(command.partnerId(), chat.getIdentifier());

        participants.save(caller);
        participants.save(partner);
        return chatMapper.toResponse(chat, true);
    }

    @Override
    public ChatResponse getOrCreate(UUID callerId, UUID partnerId) {
        ChatCreators creators = new ChatCreators(callerId, partnerId);

        return chats.findById(privateChatFactory.inspectIdentifier(creators))
                .map(new Function<Chat, ChatResponse>() {
                    @Override
                    public ChatResponse apply(Chat chat) {
                        return chatMapper.toResponse(chat, false);
                    }
                }).orElseGet(new Supplier<ChatResponse>() {
                    @Override
                    public ChatResponse get() {
                        Chat chat = privateChatFactory.create(creators, 2, null);
                        try {
                            chats.save(chat);
                            Participant caller = new Participant(callerId, chat.getIdentifier());
                            Participant partner = new Participant(partnerId, chat.getIdentifier());

                            participants.save(caller);
                            participants.save(partner);

                            em.flush();
                            return chatMapper.toResponse(chat, true);
                        } catch (DataIntegrityViolationException e) {
                            throw new ChatIdentifierUniqueException(chat.getIdentifier());
                        }
                    }
                });

    }

    @Override
    public void add(String chatId, UUID partnerId) {

        Chat chat = chats.findById(chatId).orElseThrow();
        Participant participant = new Participant(partnerId, chatId);
        chatPolicyService.apply(participant, chat);
        participants.save(participant);

    }

}
