package com.alacriti.inbound.util;

import java.util.List;

import lombok.Data;

@Data
public class Batch {
	public BatchHeader batchHeader;
	public List<EntryDetail> entryDetails;
	public BatchControl batchControl;
}