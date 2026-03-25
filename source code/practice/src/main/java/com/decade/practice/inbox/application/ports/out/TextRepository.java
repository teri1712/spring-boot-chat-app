package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextRepository extends JpaRepository<Text, Long> {
}
