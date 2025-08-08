package com.decade.practice.database.repositories;

import com.decade.practice.entities.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Repository for Admin entities.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

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