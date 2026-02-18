package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.exceptions.MessageAlreadySentException;
import com.decade.practice.engagement.application.ports.in.EventCommand;
import com.decade.practice.engagement.application.ports.in.ParticipantPlacement;
import com.decade.practice.engagement.application.ports.out.ReceiptRepository;
import com.decade.practice.engagement.domain.Receipt;
import com.decade.practice.engagement.dto.ReceiptResponse;
import com.decade.practice.engagement.dto.mapper.ReceiptMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ParticipantPlacementImpl implements ParticipantPlacement {

    private final ReceiptRepository receipts;
    private final ReceiptMapper receiptMapper;

    @PersistenceContext
    private EntityManager em;

    private void doSave(Receipt receipt) {
        try {
            receipts.save(receipt);
            em.flush();
        } catch (DataIntegrityViolationException e) {
            throw new MessageAlreadySentException(receipt.getIdempotentKey());
        }
    }

    @Override
    public ReceiptResponse place(EventCommand eventCommand) {
        Receipt receipt = receiptMapper.toEntity(eventCommand);
        if (receipt == null)
            return null;
        receipt.place();
        doSave(receipt);
        return receiptMapper.toResponse(receipt);

    }
}
