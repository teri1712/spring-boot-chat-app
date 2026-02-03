package com.decade.practice.application.usecases;

import org.springframework.core.io.Resource;

public interface IconStore {
    Resource read(int iconId);
}
