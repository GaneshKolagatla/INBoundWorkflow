package com.alacriti.inbound.serviceimpl;



import java.time.LocalDateTime;

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
}
