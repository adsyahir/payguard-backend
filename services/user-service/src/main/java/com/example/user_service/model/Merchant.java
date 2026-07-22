package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false, length = 2)
    private String country;                 // ISO-3166 alpha-2

    @Column(name = "risk_tier", nullable = false)
    private String riskTier = "low";        // low | medium | high

    @Column(nullable = false)
    private String status = "pending";      // pending | active | suspended

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void ensureUuid() {
        if (uuid == null) uuid = UUID.randomUUID();
    }
}
