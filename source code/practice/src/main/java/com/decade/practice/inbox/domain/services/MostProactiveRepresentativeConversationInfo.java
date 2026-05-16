package com.decade.practice.inbox.domain.services;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.inbox.domain.Room;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MostProactiveRepresentativeConversationInfo extends ConversationInfo {
    private final Room room;

    public MostProactiveRepresentativeConversationInfo(Room room, Set<UUID> representatives) {
        super(room.getInfo(), representatives);
        this.room = room;
    }

    @Override
    protected String deriveName(PartnerLookUp partnerLookUp) {
        var representatives = getRepresentatives();
        Iterator<UUID> iterator = representatives.iterator();
        Partner first = partnerLookUp.lookUp(iterator.next());
        StringBuilder roomName = new StringBuilder(Optional.ofNullable(first.name()).orElse(""));
        if (representatives.size() > 1) {
            while (iterator.hasNext()) {
                roomName.append(", ").append(Optional.ofNullable(partnerLookUp.lookUp(iterator.next())).map(Partner::name).orElse(""));
            }
            int remaining = room.getParticipantCount() - 1 - representatives.size();
            if (remaining > 0) {
                roomName.append(" and ").append(remaining).append(" partners");
            }
        }
        return roomName.toString();
    }

    @Override
    protected String deriveAvatar(PartnerLookUp partnerLookUp) {
        var representatives = getRepresentatives();
        return Optional.ofNullable(partnerLookUp.lookUp(representatives.iterator().next())).
            map(Partner::avatar).orElse("");
    }
}
