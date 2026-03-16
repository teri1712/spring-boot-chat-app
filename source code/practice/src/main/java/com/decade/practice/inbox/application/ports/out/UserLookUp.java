package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.users.api.UserInfo;

import java.util.Set;
import java.util.UUID;

public interface UserLookUp {
      void registerLookUp(Set<UUID> ids);

      UserInfo lookUp(UUID id);

}
