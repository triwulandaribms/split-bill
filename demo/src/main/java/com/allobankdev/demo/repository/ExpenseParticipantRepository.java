package com.allobankdev.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.demo.entity.ExpenseParticipant;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Integer> {

}