package com.decade.practice.domain.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Date;
import java.util.UUID;

@Entity
@DiscriminatorValue("ROLE_ADMIN")
public class Admin extends User {
        protected Admin() {
                super();
        }

        public Admin(String username, String password) {
                super(username, password, username, new Date(), "ROLE_ADMIN", UUID.randomUUID());
        }
}
