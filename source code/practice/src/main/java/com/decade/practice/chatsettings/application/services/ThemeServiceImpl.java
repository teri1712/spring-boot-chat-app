package com.decade.practice.chatsettings.application.services;

import com.decade.practice.chatsettings.application.ports.out.ThemeRepository;
import com.decade.practice.chatsettings.dto.ThemeMapper;
import com.decade.practice.chatsettings.dto.ThemeResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThemeServiceImpl implements ThemeService {

      private final ThemeRepository themes;
      private final ThemeMapper mapper;

      @Override
      public List<ThemeResponse> findAll() {
            return themes.findAll().stream().map(mapper::themeToResponse).collect(Collectors.toList());
      }
}
