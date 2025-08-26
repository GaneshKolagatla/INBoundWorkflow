package com.alacriti.inbound.service;

import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataPreProcessor {
	void preProcess(ACHFile achFile) throws Exception;
}
