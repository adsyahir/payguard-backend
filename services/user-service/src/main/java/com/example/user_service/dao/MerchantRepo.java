package com.example.user_service.dao;

import com.example.user_service.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepo extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByUuid(UUID uuid);
}
