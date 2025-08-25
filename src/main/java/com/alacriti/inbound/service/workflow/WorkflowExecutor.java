package com.alacriti.inbound.service.workflow;

public class WorkflowExecutor {

	private final BatchWorkFlow workFlow;

	public WorkflowExecutor(BatchWorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public void execute() throws Exception {
		//Reads file and converts that file contents into java objects
		workFlow.getFileReader().read();

		//validates file with help of created java objects by read method
		workFlow.getFileValidator().validate();

		//processes file and writes file contents to the db
		workFlow.getFileProcessor().process();
	}
}
