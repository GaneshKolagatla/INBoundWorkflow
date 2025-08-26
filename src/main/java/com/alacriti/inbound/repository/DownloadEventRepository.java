package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.DownloadEvent;

public interface DownloadEventRepository extends JpaRepository<DownloadEvent, Long> {

}