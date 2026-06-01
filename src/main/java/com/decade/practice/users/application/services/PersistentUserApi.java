package com.decade.practice.users.application.services;

import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.decade.practice.users.application.ports.out.UserRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("persistentUserApi")
public class PersistentUserApi implements UserApi {

    private final UserRepository users;

    @Override
    @Observed(name = "users.infos", lowCardinalityKeyValues = {
        "hit", "database"
    })
    public Map<UUID, UserInfo> getUserInfo(Set<UUID> ids) {
        return users.findByIdIn(ids).stream().collect(Collectors.toMap(UserInfo::id, Function.identity()));
    }
}
