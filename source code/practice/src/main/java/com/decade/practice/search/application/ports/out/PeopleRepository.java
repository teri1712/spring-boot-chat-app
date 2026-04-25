package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.People;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserDocumentRepository extends CrudRepository<People, UUID> {

}
