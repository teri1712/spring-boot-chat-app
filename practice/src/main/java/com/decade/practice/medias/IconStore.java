package com.decade.practice.medias;

import org.springframework.core.io.Resource;

public interface IconStore {
        Resource read(int resourceId);
}
