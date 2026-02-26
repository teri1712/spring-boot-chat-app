package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.query.ThemeService;
import com.decade.practice.engagement.dto.ThemeResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/themes")
@AllArgsConstructor
public class ThemeController {

      private final ThemeService themeService;

      @GetMapping
      public List<ThemeResponse> getThemes() {
            return themeService.findAll();
      }
}
