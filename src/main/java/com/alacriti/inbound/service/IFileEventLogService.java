package com.alacriti.inbound.service;



public interface IFileEventLogService {
    void logEvent(String fileName, String event, String status);
}
