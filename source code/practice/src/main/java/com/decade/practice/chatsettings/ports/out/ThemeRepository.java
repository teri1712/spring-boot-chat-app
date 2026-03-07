package com.decade.practice.chatsettings.ports.out;

import com.decade.practice.chatsettings.domain.Theme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ThemeRepository extends CrudRepository<Theme, Long> {

      @Override
      List<Theme> findAll();
}