package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ReceiptRepository extends CrudRepository<Receipt, UUID> {

}