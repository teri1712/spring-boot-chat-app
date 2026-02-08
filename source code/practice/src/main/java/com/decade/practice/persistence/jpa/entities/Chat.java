package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.generator.EventType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Chat {

    @Embedded
    private ChatCreators creators;

    @Id
    private String identifier;

    @Embedded
    @NotNull
    private Preference preference;

    @CurrentTimestamp(event = EventType.INSERT, source = SourceType.VM)
    @Temporal(TemporalType.TIMESTAMP)
    private Date interactTime;

    private int messageCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "participants", joinColumns = {@JoinColumn(name = "chat_id")}, inverseJoinColumns = {@JoinColumn(name = "participant_id")})
    // TODO: Handle fanout
    private Set<User> participants = new HashSet<>();


}