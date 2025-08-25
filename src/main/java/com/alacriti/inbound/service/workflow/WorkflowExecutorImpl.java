package com.alacriti.inbound.service.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.service.IWorkflowExecutor;

@Service
public class WorkflowExecutorImpl implements IWorkflowExecutor {

	@Autowired
	private final BatchWorkFlow workFlow;

	public WorkflowExecutorImpl(BatchWorkFlow workFlow) {
		this.workFlow = workFlow;
	}


    public void execute() throws Exception {
        //Reads file and converts that file contents into java objects
        workFlow.getFileReader().read();
        
        //validates file with help of created java objects by read method
        workFlow.getFileValidator().validate();
        
        //preprocess data before actual processing
        workFlow.getFilePreProcessor().preProcess();
        
        //processes file and writes file contents to the db
        workFlow.getFileProcessor().process();
        
        //postprocess is used for call an external rest end point which produces kafka
        //event with filename and date;
        workFlow.getFilePostProcessor().postProcess();
    }

}
