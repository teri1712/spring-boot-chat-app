package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.generator.EventType;

import java.util.Date;

@Entity
@Getter
@Setter
public class Chat {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "first_user") // for naming
    @MapsId("firstUser")
    private User firstUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "second_user") // for naming
    @MapsId("secondUser")
    private User secondUser;

    @EmbeddedId
    private ChatIdentifier identifier;

    @Embedded
    @NotNull
    private Preference preference;

    @CurrentTimestamp(event = EventType.INSERT, source = SourceType.VM)
    @Temporal(TemporalType.TIMESTAMP)
    private Date interactTime;

    private int messageCount = 0;

    // No-arg constructor required by JPA
    protected Chat() {
    }

    public Chat(User firstUser, User secondUser) {
        // Ensure firstUser.id is less than secondUser.id
        if (firstUser.getId().compareTo(secondUser.getId()) > 0) {
            User temp = firstUser;
            firstUser = secondUser;
            secondUser = temp;
        }
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.identifier = new ChatIdentifier(firstUser.getId(), secondUser.getId());
        this.preference = new Preference(firstUser, secondUser);
    }
}