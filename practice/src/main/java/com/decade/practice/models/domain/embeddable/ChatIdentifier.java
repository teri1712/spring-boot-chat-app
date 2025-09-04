package com.decade.practice.models.domain.embeddable;

import com.decade.practice.models.domain.entity.User;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ChatIdentifier implements Serializable {
        private UUID firstUser;
        private UUID secondUser;

        // No-arg constructor required by JPA
        protected ChatIdentifier() {
        }

        public ChatIdentifier(UUID firstUser, UUID secondUser) {
                this.firstUser = firstUser;
                this.secondUser = secondUser;
        }

        public UUID getFirstUser() {
                return firstUser;
        }

        public void setFirstUser(UUID firstUser) {
                this.firstUser = firstUser;
        }

        public UUID getSecondUser() {
                return secondUser;
        }

        public void setSecondUser(UUID secondUser) {
                this.secondUser = secondUser;
        }

        @Override
        public String toString() {
                return firstUser + "+" + secondUser;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ChatIdentifier that = (ChatIdentifier) o;
                return Objects.equals(firstUser, that.firstUser) &&
                        Objects.equals(secondUser, that.secondUser);
        }

        @Override
        public int hashCode() {
                return Objects.hash(firstUser, secondUser);
        }

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
}