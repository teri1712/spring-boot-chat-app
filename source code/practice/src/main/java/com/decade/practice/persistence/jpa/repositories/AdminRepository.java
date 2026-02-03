package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.entities.Admin;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


public interface AdminRepository extends ListCrudRepository<Admin, UUID> {

    default Admin getOrNull() {
        List<Admin> admins = findAll();
        return admins.isEmpty() ? null : admins.get(0);
    }

    default Admin get() {
        Admin admin = getOrNull();
        if (admin == null) {
            throw new NoSuchElementException("No admin found");
        }
        return admin;
    }
}