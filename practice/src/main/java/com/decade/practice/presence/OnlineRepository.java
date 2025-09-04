package com.decade.practice.presence;

import com.decade.practice.models.OnlineStatus;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing OnlineStatus entities.
 */
public interface OnlineRepository extends CrudRepository<OnlineStatus, String> {
}