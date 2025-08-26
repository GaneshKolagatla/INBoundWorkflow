package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alacriti.inbound.model.EntryDetails;

@Repository
public interface EntryDetailRepository extends JpaRepository<EntryDetails, Long> {
}