package com.allobankdev.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.demo.entity.BillGroup;

public interface BillGroupRepository extends JpaRepository<BillGroup, Integer> {

}