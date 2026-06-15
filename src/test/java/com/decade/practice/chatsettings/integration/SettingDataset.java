package com.decade.practice.chatsettings.integration;

import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import com.decade.practice.chatsettings.application.ports.out.ThemeRepository;
import com.decade.practice.chatsettings.domain.Theme;
import com.decade.practice.common.TestDataset;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SettingDataset implements TestDataset {

    @PersistenceContext
    EntityManager em;

    final SettingRepository settings;
    final ThemeRepository themes;

    @Override
    public void setup() {
        this.clean();
        for (int i = 100; i < 105; i++) {
            themes.save(new Theme(null, "123", i + " theme"));
        }
    }

    @Override
    public void clean() {
        settings.deleteAll();
        themes.deleteAll();
    }
}
