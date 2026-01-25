package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.entities.Theme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ThemeRepository extends CrudRepository<Theme, Integer> {

    List<Theme> findAll();
}