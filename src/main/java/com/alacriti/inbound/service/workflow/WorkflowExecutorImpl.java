package com.alacriti.inbound.service.workflow;


import java.io.File;
import java.util.Map;

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
	public void execute(Map<Long, File> map) {
		if (map == null || map.isEmpty()) {
			log.info("⚠️ No ACH files provided for workflow execution.");
			return;
		}

		for (Map.Entry<Long, File> entry : map.entrySet()) {
		    Long remoteId = entry.getKey();
		    File file = entry.getValue();

		    try {
		        log.info("🚀 Starting workflow for file: {}", file.getName());

		        // Step 1: Read
		        ACHFile achFile = workFlow.getFileReader().read(file,remoteId);

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
