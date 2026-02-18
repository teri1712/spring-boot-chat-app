package com.decade.practice.engagement.application.query;

import com.decade.practice.engagement.dto.ReceiptResponse;

import java.util.UUID;

public interface ReceiptService {
    ReceiptResponse find(UUID idempotentKey);
}
