package com.decade.practice.common;

import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SettingDataset implements TestDataset {
    final SettingRepository settings;

    @Override
    public void setup() {
    }

    @Override
    public void clean() {
        this.settings.deleteAll();
    }
}
