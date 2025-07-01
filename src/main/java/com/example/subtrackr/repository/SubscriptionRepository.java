package com.example.subtrackr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.subtrackr.model.Plan;
import com.example.subtrackr.model.Subscription;
import com.example.subtrackr.model.SubscriptionStatus;
import com.example.subtrackr.model.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByUser(User user);
    Boolean existsByUserAndPlanAndStatus(User user, Plan plan, SubscriptionStatus status);
}
