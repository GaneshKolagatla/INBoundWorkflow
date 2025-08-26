package com.alacriti.inbound.service;

import java.io.File;

import com.alacriti.inbound.util.ACHFile;

public interface IBatchDataReader {
	ACHFile read(File file) throws Exception;

}
