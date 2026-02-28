package com.decade.practice.users.application.services;

import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.decade.practice.users.application.ports.out.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserApiImpl implements UserApi {

      private final UserRepository users;

      @Override
      public Map<UUID, UserInfo> getUserInfo(Set<UUID> ids) {
            return users.findByIdIn(ids).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
      }
}
