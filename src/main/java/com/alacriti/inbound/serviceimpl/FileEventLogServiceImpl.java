package com.alacriti.inbound.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alacriti.inbound.model.FileEventLog;
import com.alacriti.inbound.repository.FileEventLogRepository;
import com.alacriti.inbound.service.IFileEventLogService;

@Service
public class FileEventLogServiceImpl implements IFileEventLogService {

	private final FileEventLogRepository repository;

	public FileEventLogServiceImpl(FileEventLogRepository repository) {
		this.repository = repository;
	}

	@Override
	public void logEvent(String fileName, String event, String status) {
		FileEventLog log = new FileEventLog();
		log.setFileName(fileName);
		log.setEvent(event);
		log.setStatus(status);
		log.setTimestamp(LocalDateTime.now());
		repository.save(log);
	}

	public List<FileEventLog> getFilesByStatus(String status) {
		return repository.findByStatus(status);
	}

	public void updateFileStatus(Long id, String newStatus) {
		FileEventLog log = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("FileEventLog not found with id " + id));
		log.setStatus(newStatus);
		log.setTimestamp(LocalDateTime.now());
		repository.save(log);
	}
}
