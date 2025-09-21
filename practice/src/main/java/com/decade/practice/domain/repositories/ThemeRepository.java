package com.decade.practice.domain.repositories;

import com.decade.practice.domain.entities.Theme;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ThemeRepository extends CrudRepository<Theme, Integer> {

        List<Theme> findAll();
}