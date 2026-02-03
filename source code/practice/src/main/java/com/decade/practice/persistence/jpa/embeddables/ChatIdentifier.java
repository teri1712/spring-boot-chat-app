package com.decade.practice.persistence.jpa.embeddables;

import com.decade.practice.persistence.jpa.entities.User;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatIdentifier implements Serializable {
    private UUID firstUser;
    private UUID secondUser;

    /**
     * Creates a ChatIdentifier from two UUIDs, ensuring the smaller UUID is always first.
     *
     * @param u1 the first UUID
     * @param u2 the second UUID
     * @return a new ChatIdentifier
     */
    public static ChatIdentifier from(UUID u1, UUID u2) {
        return u1.compareTo(u2) < 0 ?
                new ChatIdentifier(u1, u2) :
                new ChatIdentifier(u2, u1);
    }

    /**
     * Creates a ChatIdentifier from two Users, ensuring the User with the smaller ID is always first.
     *
     * @param u1 the first User
     * @param u2 the second User
     * @return a new ChatIdentifier
     */
    public static ChatIdentifier from(User u1, User u2) {
        return from(u1.getId(), u2.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChatIdentifier that = (ChatIdentifier) o;
        return Objects.equals(firstUser, that.firstUser) && Objects.equals(secondUser, that.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser);
    }

    @Override
    public String toString() {
        return firstUser + "+" + secondUser;
    }
}