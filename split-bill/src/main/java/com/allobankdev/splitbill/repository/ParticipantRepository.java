package com.allobankdev.splitbill.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allobankdev.splitbill.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    List<Participant> findByGroupId(Integer groupId);

}