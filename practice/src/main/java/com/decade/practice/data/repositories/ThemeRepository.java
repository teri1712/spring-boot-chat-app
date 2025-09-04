package com.decade.practice.data.repositories;

import com.decade.practice.models.domain.entity.Theme;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {

        @Override
        @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
        List<Theme> findAll();
}