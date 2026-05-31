package com.decade.practice;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTest {
    @Test
    void createModuleDocumentation() {

        ApplicationModules modules = ApplicationModules.of(Application.class);
        modules.verify();
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml();
    }
}
