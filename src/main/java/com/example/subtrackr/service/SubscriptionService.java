package com.example.subtrackr.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.subtrackr.dto.SubscriptionRequest;
import com.example.subtrackr.dto.SubscriptionResponse;
import com.example.subtrackr.model.Plan;
import com.example.subtrackr.model.Subscription;
import com.example.subtrackr.model.SubscriptionStatus;
import com.example.subtrackr.model.User;
import com.example.subtrackr.repository.PlanRepository;
import com.example.subtrackr.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subRepo;
    private final PlanRepository planRepo;

    private SubscriptionResponse toResponse(Subscription sub) {
        return SubscriptionResponse.builder()
            .id(sub.getId())
            .planId(sub.getPlan().getId())
            .planName(sub.getPlan().getName())
            .startAt(sub.getStartAt())
            .expiresAt(sub.getExpiresAt())
            .status(sub.getStatus().name())
            .build();
    }

    // Sottoscrive un piano d'abbonamento
    @Transactional
    public SubscriptionResponse subscribe(SubscriptionRequest req, User user) {
        Plan plan = planRepo.findById(req.getPlanId())
            .orElseThrow(() -> new IllegalArgumentException("Piano non trovato"));
        
        // Controllo che il piano non esisti già
        if(subRepo.existsByUserAndPlanAndStatus(user, plan, SubscriptionStatus.ACTIVE)) {
            throw new IllegalStateException("Hai già una sottoscrizione attiva a questo piano");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(plan.getDurationDays());

        Subscription sub = Subscription.builder()
            .user(user)
            .plan(plan)
            .startAt(now)
            .expiresAt(expiry)
            .status(SubscriptionStatus.ACTIVE)
            .build();

        return toResponse(subRepo.save(sub));
    }

    // Lista tutti i piani di un utente
    public List<SubscriptionResponse> listByUser(User user) {
        return subRepo.findByUser(user).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Cancella l'abbonamento
    @Transactional
    public void cancel(Integer id, User user) {
        Subscription sub = subRepo.findById(id)
            .filter(s -> s.getUser().equals(user))
            .orElseThrow(() -> new IllegalArgumentException("Sottoscrizione non trovata"));

        sub.setStatus(SubscriptionStatus.CANCELLED);
        subRepo.save(sub);
    }
}
