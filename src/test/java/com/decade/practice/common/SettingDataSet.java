package com.decade.practice.common;

import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SettingDataSet implements TestDataSet {
    final SettingRepository settings;

    @Override
    public void setUp() {
    }

    @Override
    public void clean() {
        this.settings.deleteAll();
    }
}
