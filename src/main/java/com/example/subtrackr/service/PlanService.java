package com.example.subtrackr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.subtrackr.dto.PlanRequest;
import com.example.subtrackr.dto.PlanResponse;
import com.example.subtrackr.model.Plan;
import com.example.subtrackr.model.User;
import com.example.subtrackr.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepo;

    // Recupera tutti i piani dell'utente
    public List<PlanResponse> getAllForUser(User user) {
        return planRepo.findByUser(user)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Recupera un singolo piano
    public PlanResponse getById(Integer id, User user) {
        Plan plan = planRepo.findById(id)
            .filter(p -> p.getUser().equals(user))
            .orElseThrow(() -> new IllegalArgumentException("Piano non trovato o utente non autorizzato"));
        return toResponse(plan);
    }

    // Crea un nuovo piano
    public PlanResponse createPlan(PlanRequest req, User user) {
        Plan plan = Plan.builder()
            .name(req.getName())
            .description(req.getDescription())
            .price(req.getPrice())
            .durationDays(req.getDurationDays())
            .user(user)
            .build();
        Plan saved = planRepo.save(plan);
        return toResponse(saved);
    }

    // Aggiorno un piano
    public PlanResponse updatePlan(Integer id, PlanRequest req, User user) {
        Plan plan = planRepo.findById(id)
            .filter(p -> p.getUser().equals(user))
            .orElseThrow(() -> new IllegalArgumentException("Piano non trovato o utente non autorizzato"));
        
        Plan updated = plan.toBuilder()
            .name(req.getName())
            .description(req.getDescription())
            .price(req.getPrice())
            .durationDays(req.getDurationDays())
            .build();

        Plan saved = planRepo.save(updated);
        return toResponse(saved);
    }

    // Elimino un piano
    public void deletePlan(Integer id, User user) {
        Plan plan = planRepo.findById(id)
            .filter(p -> p.getUser().equals(user))
            .orElseThrow(() -> new IllegalArgumentException("Piano non trovato o utente non autorizzato"));
        planRepo.delete(plan);
    }

    // Helper per mappare da entity -> DTO
    private PlanResponse toResponse(Plan plan) {
        return PlanResponse.builder()
            .id(plan.getId())
            .name(plan.getName())
            .description(plan.getDescription())
            .price(plan.getPrice())
            .durationDays(plan.getDurationDays())
            .createdAt(plan.getCreatedAt())
            .build();
    }
}
