package com.decade.practice.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public class EntityHelper {

      private EntityHelper() {
      }

      public static <T, I> T get(JpaRepository<T, I> repository, I id) {
            return repository.findById(id).orElseThrow();
      }
}