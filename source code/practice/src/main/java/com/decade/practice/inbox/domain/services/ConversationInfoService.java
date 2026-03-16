package com.decade.practice.inbox.domain.services;

import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.RoomInfo;
import com.decade.practice.users.api.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ConversationInfoService {

      private final UserLookUp userLookUp;

      @Transactional
      public ConversationInfo getInfo(UUID caller, Room room) {
            Set<UUID> representatives = pickRepresentatives(caller, room);
            userLookUp.registerLookUp(representatives);
            return fromRepresentative(representatives, room);
      }

      @Transactional
      public Map<String, ConversationInfo> getInfo(UUID caller, List<Room> rooms) {
            userLookUp.registerLookUp(rooms.stream().flatMap(new Function<Room, Stream<UUID>>() {
                  @Override
                  public Stream<UUID> apply(Room room) {
                        return pickRepresentatives(caller, room).stream();
                  }
            }).collect(Collectors.toSet()));
            Map<String, ConversationInfo> roomInfoMap = new HashMap<>();
            rooms.forEach(room -> roomInfoMap.put(room.getChatId(), fromRepresentative(pickRepresentatives(caller, room), room)));
            return roomInfoMap;
      }

      private ConversationInfo fromRepresentative(Set<UUID> representatives, Room room) {
            RoomInfo roomInfo = Optional.ofNullable(room.getInfo()).orElse(new RoomInfo(null, null));
            StringBuilder roomName = new StringBuilder(Optional.ofNullable(roomInfo.customName()).orElse(""));
            if (roomName.isEmpty()) {
                  Iterator<UUID> iterator = representatives.iterator();
                  UserInfo first = userLookUp.lookUp(iterator.next());
                  roomName = new StringBuilder(first.name());
                  if (representatives.size() > 1) {
                        while (iterator.hasNext()) {
                              roomName.append(", ").append(userLookUp.lookUp(iterator.next()).name());
                        }
                        roomName.append(" and ").append(room.getParticipantCount() - representatives.size()).append(" partners");
                  }
            }

            String avatar = roomInfo.customAvatar();
            if (avatar == null || avatar.isBlank()) {
                  avatar = userLookUp.lookUp(representatives.iterator().next()).avatar();
            }
            return new ConversationInfo(roomName.toString(), avatar);

      }

      private static Set<UUID> pickRepresentatives(UUID caller, Room room) {
            Set<UUID> representatives = new HashSet<>();
            Iterator<UUID> iterator = room.getRepresentatives().iterator();
            Optional<UUID> firstDude = findNextOtherThan(caller, iterator);
            Optional<UUID> secondDude = findNextOtherThan(caller, iterator);
            firstDude.ifPresent(representatives::add);
            secondDude.ifPresent(representatives::add);
            if (representatives.isEmpty())
                  representatives.add(room.getCreator());
            return representatives;
      }

      private static Optional<UUID> findNextOtherThan(UUID caller, Iterator<UUID> iterator) {
            if (!iterator.hasNext())
                  return Optional.empty();
            UUID next = iterator.next();
            if (next.equals(caller))
                  return findNextOtherThan(caller, iterator);
            return Optional.of(next);
      }
}
