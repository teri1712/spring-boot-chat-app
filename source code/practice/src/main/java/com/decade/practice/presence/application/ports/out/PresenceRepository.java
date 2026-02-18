package com.decade.practice.presence.application.ports.out;

import com.decade.practice.presence.domain.Presence;
import org.springframework.data.repository.CrudRepository;

public interface PresenceRepository extends CrudRepository<Presence, String> {
}