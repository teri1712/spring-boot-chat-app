package com.decade.practice.inbox.application.ports.out;

import java.util.Set;
import java.util.UUID;

public interface LookUpRegistry {
      PartnerLookUp registerLookUp(Set<UUID> ids);
}
