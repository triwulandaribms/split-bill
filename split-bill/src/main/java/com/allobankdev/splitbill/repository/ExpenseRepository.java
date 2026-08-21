package com.allobankdev.splitbill.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

}