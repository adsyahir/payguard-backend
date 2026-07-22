package com.example.user_service.dao;

import com.example.user_service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);
}
