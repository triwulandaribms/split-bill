package com.allobankdev.splitbill.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByGroupId(Integer groupId);

}