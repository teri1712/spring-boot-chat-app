package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public abstract class ConversationInfo {
    private final RoomInfo roomInfo;
    private final Set<UUID> representatives;

    protected ConversationInfo(RoomInfo roomInfo, Set<UUID> representatives) {
        this.roomInfo = roomInfo == null ? new RoomInfo(null, null) : roomInfo;
        this.representatives = representatives;
    }

    public String getName(PartnerLookUp lookUp) {
        if (this.roomInfo.customName() != null) {
            return this.roomInfo.customName();
        }
        return this.deriveName(lookUp);
    }

    public String getAvatar(PartnerLookUp lookUp) {
        if (this.roomInfo.customAvatar() != null) {
            return this.roomInfo.customAvatar();
        }
        return this.deriveAvatar(lookUp);
    }

    protected abstract String deriveName(PartnerLookUp lookUp);

    protected abstract String deriveAvatar(PartnerLookUp lookUp);
}
