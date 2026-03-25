package com.decade.practice.presence.application.ports.out;

import com.decade.practice.presence.domain.Presence;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.UUID;

public interface PresenceRepository extends KeyValueRepository<Presence, UUID> {
}