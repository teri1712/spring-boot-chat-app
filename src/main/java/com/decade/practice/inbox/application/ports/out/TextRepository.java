package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TextRepository extends JpaRepository<Text, Long> {
      List<Text> findAllBySenderIdIsIn(Collection<UUID> senderIds);
}
