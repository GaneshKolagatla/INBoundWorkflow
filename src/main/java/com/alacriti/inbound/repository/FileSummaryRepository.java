package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.FileSummaryDetails;

public interface FileSummaryRepository extends JpaRepository<FileSummaryDetails, Long> {
}
