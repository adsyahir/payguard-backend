package com.example.user_service.service;

import com.example.user_service.dao.MerchantRepo;
import com.example.user_service.dao.RoleRepo;
import com.example.user_service.dao.UserRepo;
import com.example.user_service.dto.request.LoginRequest;
import com.example.user_service.dto.request.RegisterRequest;
import com.example.user_service.dto.response.AuthResponse;
import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.model.Merchant;
import com.example.user_service.model.Role;
import com.example.user_service.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MerchantRepo merchantRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if(userRepo.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("Email already exists");
        }

        Merchant merchant = new Merchant();
        merchant.setLegalName(request.getCompanyName());
        merchant.setDisplayName(request.getCompanyName());
        merchant.setCountry(request.getCountry());
        merchantRepo.save(merchant);

        User user = new User();
        user.setMerchant(merchant);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));   // HASH here
        user.setFullName(request.getFullName());

        Role adminRole = roleRepo.findByName("MERCHANT_ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role MERCHANT_ADMIN not seeded"));
        user.getRoles().add(adminRole);

        userRepo.save(user);

        return new UserResponse(user.getUuid(), user.getEmail(), user.getFullName(), merchant.getDisplayName());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid email or password");

        if (!user.isActive())
            throw new DisabledException("Account disabled");

        String token = jwtService.issue(user);

        UserResponse userResponse = new UserResponse(
                user.getUuid(), user.getEmail(), user.getFullName(),
                user.getMerchant().getDisplayName());

        return new AuthResponse(token, "Bearer", jwtService.ttl(), userResponse);
    }

}
