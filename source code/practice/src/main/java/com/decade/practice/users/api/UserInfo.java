package com.decade.practice.users.api;

import java.util.UUID;

// TODO: Cache
public interface UserInfo {
    String getUsername();

    String getName();

    String getAvatar();

    UUID getId();

}
