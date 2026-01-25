package com.decade.practice.persistence.redis.repositories;

import com.decade.practice.persistence.redis.OnlineStatus;
import org.springframework.data.repository.CrudRepository;

public interface OnlineRepository extends CrudRepository<OnlineStatus, String> {
}