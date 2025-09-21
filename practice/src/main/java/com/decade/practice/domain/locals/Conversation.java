package com.decade.practice.domain.locals;

import com.decade.practice.domain.entities.User;
import com.decade.practice.utils.ChatUtils;

import java.util.Objects;

public class Conversation {
        private Chat chat;
        private User partner;
        private User owner;

        public Conversation(Chat chat, User partner, User owner) {
                this.chat = chat;
                this.partner = partner;
                this.owner = owner;
        }

        public Conversation() {
        }

        public Conversation(com.decade.practice.domain.entities.Chat chat, User owner) {
                this(
                        new Chat(chat, owner),
                        ChatUtils.inspectPartner(chat, owner),
                        owner
                );
        }

        public Chat getChat() {
                return chat;
        }

        public void setChat(Chat chat) {
                this.chat = chat;
        }

        public void setPartner(User partner) {
                this.partner = partner;
        }

        public void setOwner(User owner) {
                this.owner = owner;
        }

        public User getPartner() {
                return partner;
        }

        public User getOwner() {
                return owner;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Conversation that = (Conversation) o;
                return Objects.equals(chat, that.chat) &&
                        Objects.equals(partner, that.partner) &&
                        Objects.equals(owner, that.owner);
        }

        @Override
        public int hashCode() {
                return Objects.hash(chat, partner, owner);
        }

        @Override
        public String toString() {
                return "Conversation{" +
                        "chat=" + chat +
                        ", partner=" + partner +
                        ", owner=" + owner +
                        '}';
        }
}