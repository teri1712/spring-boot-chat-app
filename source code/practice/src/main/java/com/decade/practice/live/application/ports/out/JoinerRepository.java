package com.decade.practice.live.application.ports.out;

import com.decade.practice.live.domain.LiveJoiner;
import org.springframework.data.repository.CrudRepository;

public interface JoinerRepository extends CrudRepository<LiveJoiner, String> {
}