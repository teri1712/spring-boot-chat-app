package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.Theme;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
