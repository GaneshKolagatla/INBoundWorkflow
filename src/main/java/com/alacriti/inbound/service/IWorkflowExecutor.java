package com.alacriti.inbound.service;

import java.io.File;
import java.util.Map;

public interface IWorkflowExecutor {
	void execute(Map<Long,File> map) throws Exception;
}
