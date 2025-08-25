package com.alacriti.inbound.serviceimpl;

import org.springframework.stereotype.Service;

import com.alacriti.inbound.service.IBatchDataPreProcessor;
import com.alacriti.inbound.util.ACHFile;
import com.alacriti.inbound.util.Batch;
import com.alacriti.inbound.util.EntryDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchDataPreProcessorImpl implements IBatchDataPreProcessor {

	@Override
	public void preprocess(ACHFile achFile) throws Exception {
		log.info("🔄 Starting pre-processing for ACH file...");

		// Add file-level metadata
		//achFile.setProcessedTime(java.time.LocalDateTime.now());

		// Clean / normalize each entry
		for (Batch batch : achFile.getBatches()) {
			for (EntryDetail entry : batch.getEntryDetails()) {
				// Example: remove leading zeros in amount
				String amount = entry.getAmount();
				if (amount != null) {
					entry.setAmount(amount.replaceFirst("^0+(?!$)", ""));
				}

				// Example: trim name fields
				if (entry.getIndividualName() != null) {
					entry.setIndividualName(entry.getIndividualName().trim());
				}
			}
		}

		log.info("✅ Pre-processing completed for file: {}");
	}
}
