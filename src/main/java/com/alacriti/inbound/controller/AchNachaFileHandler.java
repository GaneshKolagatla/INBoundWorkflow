package com.alacriti.inbound.controller;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alacriti.inbound.model.FileEventLog;
import com.alacriti.inbound.service.workflow.WorkflowExecutorImpl;
import com.alacriti.inbound.serviceimpl.FileEventLogServiceImpl;

@RestController
public class AchNachaFileHandler {

	@Autowired
	private final FileEventLogServiceImpl fileEventLogService;
	@Autowired
	private final WorkflowExecutorImpl workflowExecutor;

	// Path where ACH files are stored after download
	private static final String DOWNLOAD_DIR = "target/decrypted-ach"; // externalize in application.properties

	public AchNachaFileHandler(FileEventLogServiceImpl fileEventLogService, WorkflowExecutorImpl workflowExecutor) {
		this.fileEventLogService = fileEventLogService;
		this.workflowExecutor = workflowExecutor;
	}

	@GetMapping("/download/process")
	public void achFileProcessor() {
		// Step 1: Get file records with "Ready to Process" status
		// Step 1: Fetch ready files
		List<FileEventLog> readyFiles = fileEventLogService.getFilesByEvents("Ready to Process");

		if (readyFiles.isEmpty()) {
		    System.out.println("No files available for processing.");
		    return;
		}

		// Step 2: Build Map<ID, File>
		Map<Long, File> fileMap = readyFiles.stream()
		        .collect(Collectors.toMap(
		                FileEventLog::getId, 
		                f -> new File(DOWNLOAD_DIR, f.getFileName())
		        ));

		// Step 3: Call workflow executor
		workflowExecutor.execute(fileMap);

		// Step 4: Update status in DB after processing
//		for (Long id : fileMap.keySet()) {
//		    fileEventLogService.updateFileEvent(id, "File Processed");
//		}

	}
}
