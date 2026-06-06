package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

      Optional<Room> findByChatId(String chatId);

      List<Room> findAllByCreatorIsIn(Collection<UUID> creators);

}
