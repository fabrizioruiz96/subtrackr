package com.example.subtrackr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.subtrackr.dto.PlanRequest;
import com.example.subtrackr.dto.PlanResponse;
import com.example.subtrackr.model.User;
import com.example.subtrackr.repository.UserRepository;
import com.example.subtrackr.service.PlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final UserRepository userRepo;

    // Lista tutti i piani dell'utente
    @GetMapping("/")
    public ResponseEntity<List<PlanResponse>> indexPlans(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        List<PlanResponse> plans = planService.getAllForUser(user);
        return ResponseEntity.ok(plans);
    }
    
    // Dettagli di un singolo piano
    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> showPlan(@PathVariable Integer id, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        PlanResponse response = planService.getById(id, user);
        return ResponseEntity.ok(response);
    }

    // Crea un nuovo piano
    @PostMapping("/")
    public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody PlanRequest req, Authentication auth) {
        log.debug("[PlanController] createPlan called with body: {}", req);
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        PlanResponse created = planService.createPlan(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Aggiorna piano esistente
    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> updatePlan(@PathVariable Integer id, @Valid @RequestBody PlanRequest req, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        PlanResponse updated = planService.updatePlan(id, req, user);
        return ResponseEntity.ok(updated);
    }
       
    // Elimina un piano
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        planService.deletePlan(id, user);
        return ResponseEntity.noContent().build();
    }
}
