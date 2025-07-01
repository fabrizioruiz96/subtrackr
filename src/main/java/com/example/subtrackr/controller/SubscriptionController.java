package com.example.subtrackr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.subtrackr.dto.SubscriptionRequest;
import com.example.subtrackr.dto.SubscriptionResponse;
import com.example.subtrackr.model.User;
import com.example.subtrackr.repository.UserRepository;
import com.example.subtrackr.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subService;
    private final UserRepository userRepo;

    // Sottoscrive un abbonamento
    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest req, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        try {
            SubscriptionResponse res = subService.subscribe(req, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }    

    // Lista gli abbonamenti di un utente
    @GetMapping
    public List<SubscriptionResponse> list(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        return subService.listByUser(user);
    }
        
    // Cancella un'abbonamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
           .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        subService.cancel(id, user);
        return ResponseEntity.noContent().build();
    }
}
