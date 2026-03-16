package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class TransactionalScopedUserLookUp implements UserLookUp {


      private static final String BATCH_LOOKUP = TransactionalScopedUserLookUp.class.getName() + ".BATCH_LOOKUP";
      private static final String LOOKUP_DICTIONARY = TransactionalScopedUserLookUp.class.getName() + ".LOOKUP_DICTIONARY";
      private final UserApi userApi;

      @Override
      public void registerLookUp(Set<UUID> ids) {
            Map<UUID, UserInfo> lookupDictionary = (Map<UUID, UserInfo>) TransactionSynchronizationManager.getResource(LOOKUP_DICTIONARY);
            if (lookupDictionary != null) {
                  ids = ids.stream().filter(id -> !lookupDictionary.containsKey(id)).collect(Collectors.toSet());
            }
            Set<UUID> lookupBatch = (Set<UUID>) TransactionSynchronizationManager.getResource(BATCH_LOOKUP);
            if (lookupBatch == null) {
                  lookupBatch = new HashSet<>(ids);
                  TransactionSynchronizationManager.bindResource(BATCH_LOOKUP, lookupBatch);
            } else {
                  lookupBatch.addAll(ids);
            }
      }

      @Override
      public UserInfo lookUp(UUID id) {
            Map<UUID, UserInfo> lookupDictionary = (Map<UUID, UserInfo>) TransactionSynchronizationManager.getResource(LOOKUP_DICTIONARY);
            if (lookupDictionary == null) {
                  lookupDictionary = new HashMap<>();
                  TransactionSynchronizationManager.bindResource(LOOKUP_DICTIONARY, lookupDictionary);
            }
            if (lookupDictionary.containsKey(id)) {
                  return lookupDictionary.get(id);
            }
            Set<UUID> lookupBatch = (Set<UUID>) TransactionSynchronizationManager.getResource(BATCH_LOOKUP);
            if (lookupBatch != null) {
                  if (!lookupBatch.contains(id))
                        throw new IllegalStateException("Lookup is not registered for " + id);
            } else {
                  lookupBatch = new HashSet<>(List.of(id));
                  TransactionSynchronizationManager.bindResource(BATCH_LOOKUP, lookupBatch);
            }
            lookupDictionary.putAll(userApi.getUserInfo(lookupBatch));
            TransactionSynchronizationManager.unbindResource(BATCH_LOOKUP);
            return lookupDictionary.get(id);
      }
}
