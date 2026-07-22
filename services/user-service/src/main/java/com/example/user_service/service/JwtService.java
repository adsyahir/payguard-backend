package com.example.user_service.service;

import com.example.user_service.model.Role;
import com.example.user_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    @Autowired
    private JwtEncoder encoder;

    @Value("${payguard.jwt.issuer}")
    private String issuer;

    @Value("${payguard.jwt.ttl-seconds}")
    private long ttl;

    public String issue(User user) {
        Instant now = Instant.now();

        // roles as ROLE_X, permissions as-is → all go in "authorities"
        List<String> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add("ROLE_" + role.getName());
            role.getPermissions().forEach(p -> authorities.add(p.getName()));
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttl))
                .subject(user.getUuid().toString())
                .claim("email", user.getEmail())
                .claim("authorities", authorities)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long ttl() { return ttl; }

}
