package com.alacriti.inbound.service;

import java.io.File;
import java.util.List;

public interface IWorkflowExecutor {
	void execute(List<File> files) throws Exception;
}
