package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Partner;

import java.util.UUID;

public interface PartnerLookUp {
      Partner lookUp(UUID id);
}
