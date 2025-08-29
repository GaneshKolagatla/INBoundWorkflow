package com.alacriti.inbound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.FileEventLog;

public interface FileEventLogRepository extends JpaRepository<FileEventLog, Long> {
	List<FileEventLog> findByEvent(String status);

}
