package com.alacriti.inbound.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.model.BatchHeaderDetails;
import com.alacriti.inbound.model.EntryDetails;
import com.alacriti.inbound.model.FileHeaderDetails;
import com.alacriti.inbound.model.FileSummaryDetails;
import com.alacriti.inbound.repository.BatchHeaderRepository;
import com.alacriti.inbound.repository.EntryDetailRepository;
import com.alacriti.inbound.repository.FileHeaderRepository;
import com.alacriti.inbound.repository.FileSummaryRepository;
import com.alacriti.inbound.service.IBatchDataProcessor;
import com.alacriti.inbound.service.IFileEventLogService;
import com.alacriti.inbound.util.ACHFile;
import com.alacriti.inbound.util.Batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchDataProcessorImpl implements IBatchDataProcessor {

	private final FileHeaderRepository fileHeaderRepository;
	private final BatchHeaderRepository batchHeaderRepository;
	private final EntryDetailRepository entryDetailRepository;
	private final FileSummaryRepository fileSummaryRepository;
	
	@Autowired
	private IFileEventLogService service;

	/**
	 * Persist ACH structures (already parsed & validated) to DB.
	 * No JPA relationships; all tables are flat.
	 */
	@Override
	public void process(ACHFile achFile) {
		
		try {
		if (achFile == null) {
			log.warn("process(): ACHFile is null — nothing to persist.");
			return;
		}

		log.info("🔹 Persisting ACH file …");

		// 1) FILE HEADER
		FileHeaderDetails fileHeader = mapFileHeader(achFile);
		fileHeader = fileHeaderRepository.save(fileHeader);
		log.info("✅ FileHeader saved (id={})", fileHeader.getId());

		// 2) BATCHES + ENTRIES
		List<Batch> batches = achFile.getBatches();
		if (batches != null && !batches.isEmpty()) {
			int batchIdx = 0;
			for (Batch b : batches) {
				batchIdx++;

				BatchHeaderDetails batchHeader = mapBatchHeader(b);
				batchHeader = batchHeaderRepository.save(batchHeader);
				log.info("✅ BatchHeader saved (id={}) [#{}]", batchHeader.getId(), batchIdx);

				if (b.getEntryDetails() != null) {
					int entryIdx = 0;
					for (com.alacriti.inbound.util.EntryDetail e : b.getEntryDetails()) {
						entryIdx++;
						EntryDetails entry = mapEntryDetail(e, b);
						entryDetailRepository.save(entry);
					}
					log.info("   ↳ {} EntryDetails saved for batch #{}", b.getEntryDetails().size(), batchIdx);
				}
			}
		} else {
			log.info("No batches found on ACH file.");
		}

		// 3) FILE SUMMARY / CONTROL
		FileSummaryDetails fileSummary = mapFileSummary(achFile); // ⬅️ no fileId parameter
		fileSummaryRepository.save(fileSummary);
		log.info("✅ FileSummary saved");

		log.info("🎉 ACH file persisted successfully.");
		service.logEvent(achFile.getFileName(), "Process", "SUCCESS");
		
		}catch(Exception e) {
			service.logEvent(achFile.getFileName(), "Process","FAILED");
		}
	}

	// ----------------- MAPPERS (util -> entity) -----------------

	private FileHeaderDetails mapFileHeader(ACHFile file) {
		FileHeaderDetails fh = new FileHeaderDetails();
		if (file.getFileHeader() != null) {
			var src = file.getFileHeader();
			fh.setRecordType(safe(src.getRecordTypeCode()));
			fh.setPriorityCode(safe(src.getPriorityCode()));
			fh.setImmediateDestination(safe(src.getImmediateDestination()));
			fh.setImmediateOrigin(safe(src.getImmediateOrigin()));
			// If your entity has LocalDate/LocalTime fields, set them in PreProcessor and stash there.
			// Here we keep string-like fields only to avoid type mismatches.
			fh.setFileIdModifier(safe(src.getFileIdModifier()));
			fh.setRecordSize(safe(src.getRecordSize()));
			fh.setBlockingFactor(safe(src.getBlockingFactor()));
			fh.setFormatCode(safe(src.getFormatCode()));
			fh.setDestinationName(safe(src.getImmediateDestinationName()));
			fh.setOriginName(safe(src.getImmediateOriginName()));
			fh.setReferenceCode(safe(src.getReferenceCode()));
		}
		// Optional columns
		fh.setFileName(null); // set in pre-processor if you want actual file name
		fh.setCreatedAt(LocalDateTime.now());
		return fh;
	}

	private BatchHeaderDetails mapBatchHeader(Batch batch) {
		BatchHeaderDetails bh = new BatchHeaderDetails();
		if (batch != null && batch.getBatchHeader() != null) {
			var src = batch.getBatchHeader();
			bh.setRecordType(safe(src.getRecordTypeCode()));
			bh.setServiceClassCode(safe(src.getServiceClassCode()));
			bh.setCompanyName(safe(src.getCompanyName()));
			bh.setCompanyDiscretionaryData(safe(src.getCompanyDiscretionaryData()));
			bh.setCompanyIdentification(safe(src.getCompanyIdentification()));
			bh.setStandardEntryClassCode(safe(src.getStandardEntryClassCode()));
			bh.setCompanyEntryDescription(safe(src.getCompanyEntryDescription()));
			bh.setCompanyDescriptiveDate(safe(src.getCompanyDescriptiveDate()));
			// Effective date often needs parsing to LocalDate in your entity; if your entity
			// uses String, keep it as-is. If it's LocalDate, parse in PreProcessor.
			bh.setSettlementDate(safe(src.getSettlementDate()));
			bh.setOriginatorStatusCode(safe(src.getOriginatorStatusCode()));
			bh.setOriginatingDFIIdentification(safe(src.getOriginatingDFI()));
			bh.setBatchNumber(safe(src.getBatchNumber()));
		}
		bh.setFileName(null);
		bh.setCreatedAt(LocalDateTime.now());
		return bh;
	}

	private EntryDetails mapEntryDetail(com.alacriti.inbound.util.EntryDetail e, Batch parentBatch) {
		EntryDetails ed = new EntryDetails();
		ed.setRecordType(safe(e.getRecordTypeCode()));
		ed.setTransactionCode(safe(e.getTransactionCode()));
		ed.setRdfiRoutingNumber(safe(e.getReceivingDFIIdentification()));
		ed.setCheckDigit(safe(e.getCheckDigit()));
		ed.setRdfiAccountNumber(safe(e.getDfiAccountNumber()));

		// amount & addenda number are numeric in many schemas; parse leniently
		ed.setAmount(parseIntSafe(e.getAmount()));
		ed.setAddendaRecordIndicator(parseIntSafe(e.getAddendaRecordIndicator()));

		ed.setIndividualIdNumber(safe(e.getIndividualIdentificationNumber()));
		ed.setIndividualName(safe(e.getIndividualName()));
		ed.setDiscretionaryData(safe(e.getDiscretionaryData()));
		ed.setTraceNumber(safe(e.getTraceNumber()));
		ed.setReceivingDFI(safe(e.getReceivingDFIIdentification()));

		// denormalized batch info if your entity has these columns
		if (parentBatch != null && parentBatch.getBatchHeader() != null) {
			ed.setBatchNumber(safe(parentBatch.getBatchHeader().getBatchNumber()));
			ed.setOriginatingDfiId(safe(parentBatch.getBatchHeader().getOriginatingDFI()));
		}

		ed.setFileName(null);
		ed.setCreatedAt(LocalDateTime.now());
		return ed;
	}

	private FileSummaryDetails mapFileSummary(ACHFile file) {
		FileSummaryDetails fs = new FileSummaryDetails();
		if (file.getFileControl() != null) {
			var src = file.getFileControl();
			fs.setRecordType(safe(src.getRecordTypeCode()));
			fs.setBatchCount(safe(src.getBatchCount()));
			fs.setBlockCount(safe(src.getBlockCount()));
			fs.setEntryAddendaCount(safe(src.getEntryAddendaCount()));
			fs.setEntryHash(safe(src.getEntryHash()));
			fs.setTotalDebitEntryDollarAmount(safe(src.getTotalDebitEntryDollarAmount()));
			fs.setTotalCreditEntryDollarAmount(safe(src.getTotalCreditEntryDollarAmount()));
			fs.setReserved(safe(src.getReserved()));
		}
		fs.setFileName(null); // put actual name in pre-processor if needed
		fs.setCreatedAt(LocalDateTime.now());
		return fs;
	}

	// ----------------- helpers -----------------
	private String safe(String v) {
		return Objects.toString(v, "");
	}

	private Integer parseIntSafe(String v) {
		try {
			if (v == null || v.isBlank())
				return 0;
			return Integer.parseInt(v.trim());
		} catch (Exception ex) {
			return 0;
		}
	}
}
