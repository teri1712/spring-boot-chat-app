package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Theme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ThemeRepository extends CrudRepository<Theme, Long> {
    @Override
    List<Theme> findAll();
}