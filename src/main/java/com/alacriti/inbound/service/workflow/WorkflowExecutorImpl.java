package com.alacriti.inbound.service.workflow;


import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.service.IWorkflowExecutor;
import com.alacriti.inbound.util.ACHFile;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class WorkflowExecutorImpl implements IWorkflowExecutor {

	@Autowired
	private BatchWorkFlow workFlow;
	
	
	
	

	public WorkflowExecutorImpl(BatchWorkFlow workFlow) {
		this.workFlow = workFlow;
	}
	public void execute(List<File> files) {
		if (files == null || files.isEmpty()) {
			log.info("⚠️ No ACH files provided for workflow execution.");
			return;
		}

		for (File file : files) {
			try {
				log.info("🚀 Starting workflow for file: {}", file.getName());

				// Step 1: Read
				ACHFile achFile = workFlow.getFileReader().read(file);

				// Step 2: Validate
				workFlow.getFileValidator().validate(achFile);

				// Step 3: Pre-process
				workFlow.getFilePreProcessor().preProcess(achFile);

				// Step 4: Process
				workFlow.getFileProcessor().process(achFile);

				// Step 5: Post-process
				workFlow.getFilePostProcessor().postProcess(achFile);

				log.info("✅ Finished workflow for file: {}", file.getName());

			} catch (Exception e) {
				log.error("❌ Error processing file {}: {}", file.getName(), e.getMessage(), e);
			}
		}

	}
}
