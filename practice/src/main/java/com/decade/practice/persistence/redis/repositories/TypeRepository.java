package com.decade.practice.persistence.redis.repositories;

import com.decade.practice.persistence.redis.TypeEvent;
import org.springframework.data.repository.CrudRepository;

public interface TypeRepository extends CrudRepository<TypeEvent, String> {
}