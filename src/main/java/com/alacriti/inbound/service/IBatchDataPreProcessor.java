package com.alacriti.inbound.service;

import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataPreProcessor {
	void preprocess(ACHFile achFile) throws Exception;
}
