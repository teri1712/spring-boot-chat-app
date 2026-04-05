package com.decade.practice.bdd.context;

import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.users.dto.ProfileResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ProfileContext {
      public ProfileResponse profile;
      public AccessToken accessToken;
}
