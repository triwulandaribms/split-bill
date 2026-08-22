package com.allobankdev.splitbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.BillGroup;

public interface BillGroupRepository extends JpaRepository<BillGroup, Integer> {

}