package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.Theme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThemeDto {
    private Integer id;
    private ImageSpec background;

    public static ThemeDto from(Theme theme) {
        ThemeDto dto = new ThemeDto();
        dto.setId(theme.getId());
        dto.setBackground(theme.getBackground());
        return dto;
    }
}
