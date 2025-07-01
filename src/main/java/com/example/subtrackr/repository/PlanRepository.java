package com.example.subtrackr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.subtrackr.model.Plan;
import com.example.subtrackr.model.User;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    List<Plan> findByUser(User user);
}
