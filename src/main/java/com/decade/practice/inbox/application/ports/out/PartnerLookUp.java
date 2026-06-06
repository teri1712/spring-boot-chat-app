package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Partner;

import java.util.Optional;
import java.util.UUID;

public interface PartnerLookUp {
    Optional<Partner> lookUp(UUID id);
}
