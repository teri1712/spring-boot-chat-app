package com.decade.practice.domain.repositories;

import com.decade.practice.domain.entities.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface ReceiptRepository extends CrudRepository<Receipt, Long> {

        Receipt findByLocalId(UUID localId);
}
