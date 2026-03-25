package com.decade.practice.inbox.domain.services;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.RoomInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ConversationInfoService {

      @Transactional
      public ConversationInfo getInfo(UUID caller, Room room, PartnerLookUp lookUp) {
            Set<UUID> representatives = pickRepresentatives(caller, room);
            return fromRepresentative(lookUp, representatives, room);
      }

      @Transactional
      public Map<String, ConversationInfo> getInfo(UUID caller, List<Room> rooms, PartnerLookUp lookUp) {
            Map<String, ConversationInfo> roomInfoMap = new HashMap<>();
            rooms.forEach(room -> roomInfoMap.put(room.getChatId(), fromRepresentative(lookUp, pickRepresentatives(caller, room), room)));
            return roomInfoMap;
      }

      private ConversationInfo fromRepresentative(PartnerLookUp partnerLookUp, Set<UUID> representatives, Room room) {
            RoomInfo roomInfo = Optional.ofNullable(room.getInfo()).orElse(new RoomInfo(null, null));
            StringBuilder roomName = new StringBuilder(Optional.ofNullable(roomInfo.customName()).orElse(""));
            if (roomName.isEmpty()) {
                  Iterator<UUID> iterator = representatives.iterator();
                  Partner first = partnerLookUp.lookUp(iterator.next());
                  roomName = new StringBuilder(first.name());
                  if (representatives.size() > 1) {
                        while (iterator.hasNext()) {
                              roomName.append(", ").append(partnerLookUp.lookUp(iterator.next()).name());
                        }
                        int remaining = room.getParticipantCount() - 1 - representatives.size();
                        if (remaining > 0) {
                              roomName.append(" and ").append(remaining).append(" partners");
                        }
                  }
            }

            String avatar = roomInfo.customAvatar();
            if (avatar == null || avatar.isBlank()) {
                  avatar = partnerLookUp.lookUp(representatives.iterator().next()).avatar();
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
