package com.decade.practice.bdd.context;

import com.decade.practice.users.dto.AccessToken;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ChangePasswordContext {
      public int status;
      public AccessToken oldToken;
}
