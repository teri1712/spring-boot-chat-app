package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.Theme;
import com.decade.practice.domain.repositories.ThemeRepository;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface JpaThemeRepository extends ThemeRepository, JpaRepository<Theme, Integer> {

        @Override
        @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
        List<Theme> findAll();
}
