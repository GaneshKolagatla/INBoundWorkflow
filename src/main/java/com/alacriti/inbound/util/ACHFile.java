package com.alacriti.inbound.util;

import java.util.List;

import lombok.Data;

@Data
public class ACHFile {
	public FileHeader fileHeader;
	public List<Batch> batches;
	public FileControl fileControl;
	public String fileName;
	public String creationDate;
}