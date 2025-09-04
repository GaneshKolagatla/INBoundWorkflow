package com.alacriti.inbound.serviceimpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.service.IBatchNachaFileDownloader;
import com.alacriti.inbound.service.IDownloadMetadataInfo;

@Service
public class BatchNachaFileDownloaderImpl implements IBatchNachaFileDownloader<IDownloadMetadataInfo> {

	@Autowired
	SftpDownloadService sftpDownloadService;
	
	
//	@Override
//	public List<File> download(IDownloadMetadataInfo metadata,int days) throws Exception {
//		Files.createDirectories(Paths.get(metadata.getDownloadDir()));
//		Files.createDirectories(Paths.get(metadata.getDecryptDir()));
//
//		// Use today's date automatically
//		String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
//
//		sftpDownloadService.downloadAndDecryptByDate(metadata.getSftpCredentialObj(), today, metadata.getDownloadDir(),metadata.getDecryptDir(),metadata.getPrivateKeyDir(),
//				metadata.getPassPhrase());
//
//		File[] decryptedFiles = new File(metadata.getDecryptDir()).listFiles(File::isFile);
//		return decryptedFiles == null ? List.of() : Arrays.asList(decryptedFiles);
//
//	}
	
	@Override
	public List<File> download(IDownloadMetadataInfo metadata, int days) throws Exception {
	    Files.createDirectories(Paths.get(metadata.getDownloadDir()));
	    Files.createDirectories(Paths.get(metadata.getDecryptDir()));

	    // Calculate date range - List of strings containing dates in yyyyMMdd format
	    LocalDate endDate = LocalDate.now();
	    LocalDate startDate = endDate.minusDays(days - 1);

	    // Generate list of date strings for filtering
	    List<String> dateStrings = new ArrayList<>();
	    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
	        dateStrings.add(date.format(DateTimeFormatter.BASIC_ISO_DATE));
	    }

	    // Pass list of dates instead of a single date
	    sftpDownloadService.downloadAndDecryptByDates(metadata.getSftpCredentialObj(),
	            dateStrings,
	            metadata.getDownloadDir(),
	            metadata.getDecryptDir(),
	            metadata.getPrivateKeyDir(),
	            metadata.getPassPhrase());

	    File[] decryptedFiles = new File(metadata.getDecryptDir()).listFiles(File::isFile);
	    return decryptedFiles == null ? List.of() : Arrays.asList(decryptedFiles);
	}

}

	

		
