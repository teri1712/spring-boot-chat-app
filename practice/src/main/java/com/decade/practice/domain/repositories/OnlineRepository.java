package com.decade.practice.domain.repositories;

import com.decade.practice.domain.OnlineStatus;
import org.springframework.data.repository.CrudRepository;

public interface OnlineRepository extends CrudRepository<OnlineStatus, String> {
}