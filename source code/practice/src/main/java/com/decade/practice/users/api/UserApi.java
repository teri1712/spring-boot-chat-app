package com.decade.practice.users.api;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserApi {
      Map<UUID, UserInfo> getUserInfo(Set<UUID> ids);
}
