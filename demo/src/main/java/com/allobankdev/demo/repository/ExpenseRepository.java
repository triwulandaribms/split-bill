package com.allobankdev.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.demo.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

}