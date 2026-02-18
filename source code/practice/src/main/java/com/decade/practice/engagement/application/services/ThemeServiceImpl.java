package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.out.ThemeRepository;
import com.decade.practice.engagement.application.query.ThemeService;
import com.decade.practice.engagement.dto.ThemeResponse;
import com.decade.practice.engagement.dto.mapper.ThemeMapper;
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
