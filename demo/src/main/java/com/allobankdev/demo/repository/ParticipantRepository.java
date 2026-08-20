package com.allobankdev.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.demo.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

}