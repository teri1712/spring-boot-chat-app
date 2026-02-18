package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.out.ReceiptRepository;
import com.decade.practice.engagement.application.query.ReceiptService;
import com.decade.practice.engagement.dto.ReceiptResponse;
import com.decade.practice.engagement.dto.mapper.ReceiptMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receipts;
    private final ReceiptMapper receiptMapper;

    @Override
    public ReceiptResponse find(UUID idempotentKey) {
        return receiptMapper.toResponse(receipts.findById(idempotentKey).orElseThrow());
    }
}
