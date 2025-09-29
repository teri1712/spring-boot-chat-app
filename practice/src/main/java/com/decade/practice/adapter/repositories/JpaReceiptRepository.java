package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.Receipt;
import com.decade.practice.domain.repositories.ReceiptRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReceiptRepository extends ReceiptRepository, JpaRepository<Receipt, Long> {
}
