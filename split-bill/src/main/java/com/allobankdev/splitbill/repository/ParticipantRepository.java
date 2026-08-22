package com.allobankdev.splitbill.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

}