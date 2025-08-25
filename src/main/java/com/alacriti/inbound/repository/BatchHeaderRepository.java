package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alacriti.inbound.model.BatchHeaderDetails;

@Repository
public interface BatchHeaderRepository extends JpaRepository<BatchHeaderDetails, Long> {
}