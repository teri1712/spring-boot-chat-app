package com.decade.practice.chatsettings.adapter;

import com.decade.practice.chatsettings.application.services.ThemeService;
import com.decade.practice.chatsettings.dto.ThemeResponse;
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
