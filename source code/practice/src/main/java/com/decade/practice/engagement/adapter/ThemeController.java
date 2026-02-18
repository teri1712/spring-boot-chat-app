package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.ports.out.ThemeRepository;
import com.decade.practice.engagement.domain.Theme;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/themes")
@AllArgsConstructor
public class ThemeController {

    private final ThemeRepository themeRepository;

    @GetMapping
    public List<Theme> getThemes() {
        return themeRepository.findAll();
    }
}
