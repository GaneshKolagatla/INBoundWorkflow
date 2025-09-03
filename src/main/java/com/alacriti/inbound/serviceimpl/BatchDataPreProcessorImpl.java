package com.alacriti.inbound.serviceimpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.service.IBatchDataPreProcessor;
import com.alacriti.inbound.service.IFileEventLogService;
import com.alacriti.inbound.util.ACHFile;
import com.alacriti.inbound.util.Batch;
import com.alacriti.inbound.util.EntryDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchDataPreProcessorImpl implements IBatchDataPreProcessor {

	@Autowired
	IFileEventLogService service;

	@Override
	public void preProcess(ACHFile achFile) throws Exception {
		try {
			log.info("🔄 Starting pre-processing for ACH file...");

			if (achFile == null) {
				log.warn("ACHFile is null, skipping pre-processing.");
				return;
			}

			// ✅ set file metadata (you can wire fileName from your download logic)
			if (achFile.getFileName() == null) {
				achFile.setFileName("ACH_" + System.currentTimeMillis() + ".ach");
			}

			// ✅ optional: set creationDate if not already set
			if (achFile.getCreationDate() == null) {
				achFile.setCreationDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
			}

			// ✅ clean / normalize each entry
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

			log.info("✅ Pre-processing completed for file: {}", achFile.getFileName());
			service.updateFileEvent(achFile.getRemoteId(), "FILE-Pre-PROCESSED", "SUCCESS");

		} catch (Exception e) {
			log.error("❌ Pre-processing failed", e);
			service.updateFileEvent(achFile != null ? achFile.getRemoteId() : null, "FILE-Pre-PROCESSED", "FAILED");
		}
	}
}
