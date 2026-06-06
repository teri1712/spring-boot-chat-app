package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SimpleLookUpRegistry implements LookUpRegistry {

    private final UserApi userApi;

    @Override
    public PartnerLookUp registerLookUp(Set<UUID> ids) {
        return new BatchLookUp(ids);
    }

    class BatchLookUp implements PartnerLookUp {

        private final Map<UUID, Partner> lookupDictionary = new HashMap<>();

        BatchLookUp(Set<UUID> ids) {
            for (UserInfo info : userApi.getUserInfo(ids).values()) {
                lookupDictionary.put(info.id(), new Partner(info.id(), info.name(), info.avatar()));
            }
        }

        @Override
        public Optional<Partner> lookUp(UUID id) {
            return Optional.ofNullable(lookupDictionary.get(id));
        }

    }
}
