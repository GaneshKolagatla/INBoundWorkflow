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

					BatchHeaderDetails batchHeader = mapBatchHeader(b, achFile);
					batchHeader = batchHeaderRepository.save(batchHeader);
					log.info("✅ BatchHeader saved (id={}) [#{}]", batchHeader.getId(), batchIdx);

					if (b.getEntryDetails() != null) {
						int entryIdx = 0;
						for (com.alacriti.inbound.util.EntryDetail e : b.getEntryDetails()) {
							entryIdx++;
							EntryDetails entry = mapEntryDetail(e, b, achFile);
							entryDetailRepository.save(entry);
						}
						log.info("   ↳ {} EntryDetails saved for batch #{}", b.getEntryDetails().size(), batchIdx);
					}
				}
			} else {
				log.info("No batches found on ACH file.");
			}

			// 3) FILE SUMMARY / CONTROL
			FileSummaryDetails fileSummary = mapFileSummary(achFile);
			fileSummaryRepository.save(fileSummary);
			log.info("✅ FileSummary saved");

			log.info("🎉 ACH file persisted successfully.");
			service.updateFileEvent(achFile.getRemoteId(), "FILE-PROCESSED", "SUCCESS");

		} catch (Exception e) {
			log.error("❌ Error persisting ACH file", e);
			service.updateFileEvent(achFile != null ? achFile.getRemoteId() : null, "FILE-PROCESSED", "FAILED");
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
			fh.setFileIdModifier(safe(src.getFileIdModifier()));
			fh.setRecordSize(safe(src.getRecordSize()));
			fh.setBlockingFactor(safe(src.getBlockingFactor()));
			fh.setFormatCode(safe(src.getFormatCode()));
			fh.setDestinationName(safe(src.getImmediateDestinationName()));
			fh.setOriginName(safe(src.getImmediateOriginName()));
			fh.setReferenceCode(safe(src.getReferenceCode()));
			fh.setFileCreationDate(safe(src.getFileCreationDate())); // YYMMDD from record
			fh.setFileCreationTime(safe(src.getFileCreationTime()));
			fh.setReferenceCode(safe(src.getReferenceCode())); // was missing

		}
		fh.setFileName(file.getFileName());

		fh.setCreatedAt(LocalDateTime.now());
		return fh;
	}

	private BatchHeaderDetails mapBatchHeader(Batch batch, ACHFile file) {
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
			// ✅ include this (you were missing it)
			bh.setEffectiveEntryDate(safe(src.getEffectiveEntryDate()));
			bh.setSettlementDate(safe(src.getSettlementDate()));
			bh.setOriginatorStatusCode(safe(src.getOriginatorStatusCode()));
			bh.setOriginatingDFIIdentification(safe(src.getOriginatingDFI()));
			bh.setBatchNumber(safe(src.getBatchNumber()));
		}
		bh.setFileName(file.getFileName());
		bh.setCreatedAt(LocalDateTime.now());
		return bh;
	}

	private EntryDetails mapEntryDetail(com.alacriti.inbound.util.EntryDetail e, Batch parentBatch, ACHFile file) {
		EntryDetails ed = new EntryDetails();
		ed.setRecordType(safe(e.getRecordTypeCode()));
		ed.setTransactionCode(safe(e.getTransactionCode()));
		ed.setRdfiRoutingNumber(safe(e.getReceivingDFIIdentification()));
		ed.setCheckDigit(safe(e.getCheckDigit()));
		ed.setRdfiAccountNumber(safe(e.getDfiAccountNumber()));
		ed.setAmount(parseIntSafe(e.getAmount()));
		ed.setAddendaRecordIndicator(parseIntSafe(e.getAddendaRecordIndicator()));
		ed.setIndividualIdNumber(safe(e.getIndividualIdentificationNumber()));
		ed.setIndividualName(safe(e.getIndividualName()));
		ed.setDiscretionaryData(safe(e.getDiscretionaryData()));
		ed.setTraceNumber(safe(e.getTraceNumber()));
		ed.setReceivingDFI(safe(e.getReceivingDFIIdentification()));

		if (parentBatch != null && parentBatch.getBatchHeader() != null) {
			ed.setBatchNumber(safe(parentBatch.getBatchHeader().getBatchNumber()));
			ed.setOriginatingDfiId(safe(parentBatch.getBatchHeader().getOriginatingDFI()));
		}

		ed.setFileName(file.getFileName());
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
		fs.setFileName(file.getFileName());
		fs.setCreatedAt(LocalDateTime.now());
		return fs;
	}

	/*----------------- helpers -----------------*/
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
