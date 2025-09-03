package com.alacriti.inbound.util;

import java.util.List;

import lombok.Data;

@Data
public class ACHFile {
	public FileHeader fileHeader;
	public List<Batch> batches;
	public FileControl fileControl;
	public String fileName; // just the file name, e.g. sample.ach
	public String sourceFilePath; // full path, e.g. /tmp/inbound/sample.ach
	public String creationDate;
	public Long remoteId;
}
