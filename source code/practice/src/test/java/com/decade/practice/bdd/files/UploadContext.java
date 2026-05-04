package com.decade.practice.bdd.files;

import com.decade.practice.resources.files.api.FileIntegrity;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class UploadContext {
    public FileIntegrity integrity;
}
