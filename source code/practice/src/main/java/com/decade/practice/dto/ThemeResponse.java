package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.Theme;

public record ThemeResponse(Integer id, ImageSpec background) {
    public static ThemeResponse from(Theme theme) {
        if (theme == null) return null;
        return new ThemeResponse(theme.getId(), theme.getBackground());
    }
}
