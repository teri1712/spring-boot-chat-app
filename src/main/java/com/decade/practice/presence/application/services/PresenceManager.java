package com.decade.practice.presence.application.services;

import com.decade.practice.presence.application.ports.out.PresenceRepository;
import com.decade.practice.presence.application.ports.out.ScoreEngine;
import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.domain.Presence;
import com.decade.practice.presence.dto.BuddyResponse;
import com.decade.practice.presence.dto.RoomPresenceResponse;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PresenceManager implements PresenceService {

      private final PresenceRepository presences;
      private final ScoreEngine scoreEngine;
      private final UserApi userApi;

      @Override
      public Map<String, RoomPresenceResponse> find(UUID caller, Set<String> chatIds) {
            Map<String, List<String>> enthusiastMap = scoreEngine.findTopK(chatIds, 10);
            Map<UUID, Set<String>> correspondingChatMap = new HashMap<>();
            enthusiastMap.forEach((chatId, enthusiasts) -> {
                  enthusiasts.forEach(enthusiast -> correspondingChatMap.compute(UUID.fromString(enthusiast),
                            (id, chats) -> {
                                  if (chats == null) {
                                        chats = new HashSet<>();
                                  }
                                  chats.add(chatId);
                                  return chats;
                            }));
            });

            Map<String, RoomPresenceResponse> roomPresences = new HashMap<>();

            List<UUID> allEnthusiasts = enthusiastMap.values().parallelStream()
                      .flatMap(new Function<List<String>, Stream<String>>() {
                            @Override
                            public Stream<String> apply(List<String> enthusiasts) {
                                  return enthusiasts.parallelStream();
                            }
                      })
                      .map(UUID::fromString)
                      .distinct()
                      .filter(enthusiast -> !enthusiast.equals(caller))
                      .toList();


            List<Presence> enthusiastPresences = this.presences.findAllById(allEnthusiasts);

            enthusiastPresences.forEach(presence -> {
                  Set<String> correspondingChatIds = correspondingChatMap.get(presence.getUserId());
                  correspondingChatIds.forEach(correspondingChatId -> {

                        roomPresences.compute(correspondingChatId, new BiFunction<>() {
                              @Override
                              public RoomPresenceResponse apply(String chatId, RoomPresenceResponse existing) {
                                    Instant at = presence.getAt();
                                    if (existing != null) {
                                          at = at.isAfter(existing.at())
                                                    ? at : existing.at();
                                    }
                                    return new RoomPresenceResponse(chatId, at);
                              }
                        });
                  });
            });
            return roomPresences;
      }


      @Override
      public List<BuddyResponse> findMyBuddies(UUID userId) {
            List<UUID> buddies = scoreEngine.findTopK(userId.toString(), 20)
                      .stream().map(UUID::fromString).toList();
            Map<UUID, UserInfo> infoMap = userApi.getUserInfo(new HashSet<>(buddies));
            Map<UUID, Presence> presenceMap = presences.findAllById(buddies)
                      .stream().collect(Collectors.toMap(Presence::getUserId, Function.identity()));
            return buddies.stream()
                      .map(new Function<UUID, BuddyResponse>() {
                            @Override
                            public BuddyResponse apply(UUID buddy) {
                                  Presence presence = presenceMap.get(buddy);
                                  UserInfo info = infoMap.get(buddy);
                                  if (presence == null || info == null)
                                        return null;
                                  return new BuddyResponse(presence.getUserId(), info.name(), info.avatar(), presence.getAt());
                            }
                      }).filter(Objects::nonNull).toList();
      }
}