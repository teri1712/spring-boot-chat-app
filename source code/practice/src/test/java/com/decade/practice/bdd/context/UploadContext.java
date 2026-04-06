package com.decade.practice.bdd.context;

import com.decade.practice.resources.files.api.FileIntegrity;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class UploadContext {
      public int finishStatus;
      public FileIntegrity integrity;
}
