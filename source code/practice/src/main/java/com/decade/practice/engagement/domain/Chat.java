package com.decade.practice.engagement.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class Chat {

    @Embedded
    private ChatCreators creators;

    @Id
    private String identifier;

    @Embedded
    @NotNull
    private Preference preference;

    @Embedded
    private ChatPolicy policy;

    public Chat(ChatCreators creators, String identifier, Preference preference, ChatPolicy policy) {
        this.creators = creators;
        this.identifier = identifier;
        this.preference = preference;
        this.policy = policy;
    }


    @Version
    private Integer version;

    protected Chat() {
    }

    public Preference updateIcon(Integer iconId) {
        this.preference = new Preference(iconId, preference.roomName(), preference.roomAvatar(), preference.theme());
        return this.preference;
    }

    public Preference updateRoomName(String roomName) {
        this.preference = new Preference(preference.iconId(), roomName, preference.roomAvatar(), preference.theme());
        return this.preference;
    }

    public Preference updateTheme(String theme) {
        this.preference = new Preference(preference.iconId(), preference.roomName(), preference.roomAvatar(), theme);
        return this.preference;
    }

    public Preference updateAvatar(String roomAvatar) {
        this.preference = new Preference(preference.iconId(), preference.roomName(), roomAvatar, preference.theme());
        return this.preference;
    }
}