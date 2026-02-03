package com.decade.practice.application.services;

import com.decade.practice.application.usecases.TempResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Component
public class TransactionalTempResource implements TempResource {

    private String getResourceName() {
        return (String) TransactionSynchronizationManager.getResource(getClass().getName());
    }

    @Override
    public Object get() {
        String resourceName = getResourceName();
        return resourceName == null ? null : TransactionSynchronizationManager.getResource(getResourceName());
    }

    @Override
    public void put(Object resource) {
        if (getResourceName() != null) {
            TransactionSynchronizationManager.unbindResource(getResourceName());
            TransactionSynchronizationManager.unbindResource(getClass().getName());
        }
        String realName = UUID.randomUUID().toString();
        TransactionSynchronizationManager.bindResource(getClass().getName(), realName);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (TransactionSynchronizationManager.hasResource(getResourceName())) {
                    TransactionSynchronizationManager.unbindResource(getResourceName());
                    TransactionSynchronizationManager.unbindResource(getClass().getName());
                }
            }
        });
    }
}
