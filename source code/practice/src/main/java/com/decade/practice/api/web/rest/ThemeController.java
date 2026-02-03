package com.decade.practice.api.web.rest;

import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
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
