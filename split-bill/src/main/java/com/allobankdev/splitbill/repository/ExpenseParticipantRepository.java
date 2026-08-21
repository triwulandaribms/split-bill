package com.allobankdev.splitbill.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.ExpenseParticipant;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Integer> {

    List<ExpenseParticipant> findByExpenseId(Integer expenseId);

}