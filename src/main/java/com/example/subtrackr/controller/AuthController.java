package com.example.subtrackr.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.subtrackr.dto.AuthenticationRequest;
import com.example.subtrackr.dto.AuthenticationResponse;
import com.example.subtrackr.dto.RegisterRequest;
import com.example.subtrackr.model.Role;
import com.example.subtrackr.model.User;
import com.example.subtrackr.repository.UserRepository;
import com.example.subtrackr.security.jwt.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passEncoder;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
        @Valid @RequestBody RegisterRequest req) {

        // Verifico l'esistenza della mail
        if(userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                .body("Error: Email già in uso");
        }

        // Creo e salvo l'utente
        User user = User.builder()
            .name(req.getName())
            .email(req.getEmail())
            .password(passEncoder.encode(req.getPassword()))
            .role(Role.USER)
            .createdAt(LocalDateTime.now())
            .build();
        userRepo.save(user);

        // Genero il token
        String token = jwtService.generateToken(
            org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build());

        return ResponseEntity.ok(new AuthenticationResponse(token, "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest req) {

        // Autentico le credenziali
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    req.getEmail(),
                    req.getPassword())
            );
        
        // Genero Token
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.User) 
            authentication.getPrincipal());

        return ResponseEntity.ok(new AuthenticationResponse(token, "Bearer"));
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication auth) {
        // Restituisco i dati dell'utente corrente
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow();
        user.setPassword(null); // Non mostro la password
        return ResponseEntity.ok(user);
    }
    
}
