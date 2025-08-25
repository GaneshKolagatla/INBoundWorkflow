package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.FileEventLog;

public interface FileEventLogRepository extends JpaRepository<FileEventLog, Long> {
}
