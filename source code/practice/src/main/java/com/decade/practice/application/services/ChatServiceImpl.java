package com.decade.practice.application.services;

import com.decade.practice.application.exception.OutdatedVersionException;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.dto.ChatDetails;
import com.decade.practice.dto.ChatSnapshot;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.mapper.ChatMapper;
import com.decade.practice.dto.mapper.ConversationMapper;
import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatOrder;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import com.decade.practice.utils.ChatUtils;
import com.decade.practice.utils.EventUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service("chatService")
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl extends SelfAwareBean implements ChatService {

    private final UserRepository userRepo;
    private final EventService eventService;
    private final ChatRepository chatRepo;
    private final ChatOrderRepository chatOrderRepo;
    private final ConversationMapper conversationMapper;
    private final ChatMapper chatMapper;

    @PersistenceContext
    private EntityManager em;


    @Override
    public ChatDetails createChat(String chatId, UUID userId, String roomName, Integer iconId, UUID withPartner) {
        User user = userRepo.findById(userId).orElseThrow();
        User partner = userRepo.findById(withPartner).orElseThrow();

        ChatCreators creators = new ChatCreators(user, partner);

        Chat chat = new Chat();

        chat.setIdentifier(chatId);
        chat.setCreators(creators);
        chat.setIdentifier(chatId);

        Preference preference = new Preference();
        preference.setRoomName(roomName);
        preference.setIconId(iconId);
        chat.setPreference(preference);

        chat.getParticipants().add(user);
        chat.getParticipants().add(partner);
        return chatMapper.toChatDetails(chat, partner);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public List<Chat> listChat(UUID userId, Integer version, Optional<String> offset, int limit) {
        User owner = userRepo.findById(userId).orElseThrow();
        if (owner.getSyncContext().getEventVersion() != version)
            throw new OutdatedVersionException(owner.getSyncContext().getEventVersion(), version);
        Optional<ChatOrder> order = offset.flatMap((Function<String, Optional<ChatOrder>>) chatIdentifier -> chatOrderRepo.findByChat_IdentifierAndOwner(chatIdentifier, owner));

        if (order.isPresent()) {
            List<ChatOrder> chatOrders = chatOrderRepo.findByOwnerAndCurrentVersionLessThan(owner, order.get().getCurrentVersion(), PageRequest.of(0, limit, EventUtils.CURRENT_SORT_DESC));

            return chatOrders.stream().map(ChatOrder::getChat).toList();
        }
        List<ChatOrder> chatOrders = chatOrderRepo.findByOwnerAndCurrentVersionLessThan(owner, version + 1, PageRequest.of(0, limit, EventUtils.CURRENT_SORT_DESC));
        return chatOrders.stream().map(ChatOrder::getChat).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("@accessPolicy.isAllowed(#chatId,#userId)")
    public ChatSnapshot getSnapshot(String chatId, UUID userId, int atVersion) {
        User owner = userRepo.findById(userId).orElseThrow();
        Chat chat = chatRepo.findById(chatId).orElseThrow();
        List<EventResponse> eventList = eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatId, atVersion);
        return new ChatSnapshot(
                conversationMapper.toConversation(chat, owner),
                eventList,
                atVersion
        );
    }

    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#chatId,#userId)")
    public ChatDetails getDetails(String chatId, UUID userId) {
        User owner = userRepo.findById(userId).orElseThrow();
        // TODO: N + 1 resolve
        Chat chat = chatRepo.findById(chatId).orElseThrow();
        return chatMapper.toChatDetails(chat, ChatUtils.inspectPartner(chat, owner));
    }
}
