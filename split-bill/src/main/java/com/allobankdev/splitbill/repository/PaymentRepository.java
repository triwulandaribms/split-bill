package com.allobankdev.splitbill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Integer> {

    List<Payment> findByGroupId(Integer groupId);
}