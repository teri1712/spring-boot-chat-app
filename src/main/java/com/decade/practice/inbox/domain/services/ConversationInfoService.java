package com.decade.practice.inbox.domain.services;

import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Room;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConversationInfoService {

    public Map<String, ConversationInfo> getInfo(UUID caller, List<Room> rooms) {
        return rooms.stream().collect(Collectors.toMap(
            Room::getChatId,
            room -> getInfo(caller, room)
        ));
    }

    public ConversationInfo getInfo(UUID caller, Room room) {
        Set<UUID> representatives = pickRepresentatives(caller, room);
        return new MostProactiveRepresentativeConversationInfo(room, representatives);
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
