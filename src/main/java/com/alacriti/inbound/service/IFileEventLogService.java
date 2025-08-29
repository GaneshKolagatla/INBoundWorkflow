package com.alacriti.inbound.service;

import java.util.List;

import com.alacriti.inbound.model.FileEventLog;

public interface IFileEventLogService {
    void logEvent(String fileName, String event, String status);
    List<FileEventLog> getFilesByEvents(String status);
    void updateFileEvent(Long id, String newEvent,String newStatus);
    
    
}
