package com.decade.practice.engagement.application.query;

import com.decade.practice.engagement.dto.ThemeResponse;

import java.util.List;

public interface ThemeService {
    List<ThemeResponse> findAll();
}
