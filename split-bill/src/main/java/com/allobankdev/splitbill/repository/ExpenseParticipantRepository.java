package com.allobankdev.splitbill.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.ExpenseParticipant;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Integer> {

}