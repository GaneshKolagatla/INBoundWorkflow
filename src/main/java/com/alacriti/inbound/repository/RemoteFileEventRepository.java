package com.alacriti.inbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.RemoteFileEvent;

public interface RemoteFileEventRepository extends JpaRepository<RemoteFileEvent, Long> {
	long countByEventType(String eventType);
}
