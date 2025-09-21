package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.Admin;
import com.decade.practice.domain.repositories.AdminRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface JpaAdminRepository extends AdminRepository, JpaRepository<Admin, UUID> {

        @Override
        default Admin getOrNull() {
                List<Admin> admins = findAll();
                return admins.isEmpty() ? null : admins.get(0);
        }

        @Override
        default Admin get() {
                Admin admin = getOrNull();
                if (admin == null) {
                        throw new NoSuchElementException("No admin found");
                }
                return admin;
        }
}
