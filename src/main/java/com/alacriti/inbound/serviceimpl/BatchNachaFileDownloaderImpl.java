package com.alacriti.inbound.serviceimpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	@Override
	public List<File> download(IDownloadMetadataInfo metadata) throws Exception {
		Files.createDirectories(Paths.get(metadata.getDownloadDir()));
		Files.createDirectories(Paths.get(metadata.getDecryptDir()));

		// Use today's date automatically
		String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

		sftpDownloadService.downloadAndDecryptByDate(metadata.getSftpCredentialObj(), today, metadata.getDownloadDir(),metadata.getDecryptDir(),metadata.getPrivateKeyDir(),
				metadata.getPassPhrase());

		File[] decryptedFiles = new File(metadata.getDecryptDir()).listFiles(File::isFile);
		return decryptedFiles == null ? List.of() : Arrays.asList(decryptedFiles);

	}
}

	

		
