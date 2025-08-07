package com.decade.practice.online;

import com.decade.practice.entities.OnlineStatus;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing OnlineStatus entities.
 */
public interface OnlineRepository extends CrudRepository<OnlineStatus, String> {
}