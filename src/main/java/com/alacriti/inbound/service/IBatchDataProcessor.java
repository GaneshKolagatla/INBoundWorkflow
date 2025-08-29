package com.alacriti.inbound.service;

import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataProcessor {
	
	void process(ACHFile achFile) throws Exception;
	
}
