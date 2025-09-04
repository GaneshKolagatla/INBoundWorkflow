package com.alacriti.inbound.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.model.RemoteFile;
import com.alacriti.inbound.model.SftpServerCredentials;
import com.alacriti.inbound.repository.RemoteFileRepository;
import com.alacriti.inbound.service.IFileEventLogService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SftpDownloadService {

	private final PGPDecryptionService pgpEncryptionService;

	@Autowired
	IFileEventLogService service;

	public SftpDownloadService(PGPDecryptionService pgpEncryptionService) {
		this.pgpEncryptionService = pgpEncryptionService;
	}
	@Autowired
	RemoteFileRepository remoteFileRepository;
	



	
	// SftpDownloadService.java
	public List<File> downloadAndDecryptByDates(
	        SftpServerCredentials config,
	        List<String> dateStrings,
	        String downloadDir,
	        String decryptedDir,
	        String privateKeyPath,
	        String passphrase) throws Exception {

	    List<File> decryptedThisRun = new ArrayList<>();   // <--- keep only this run's files

	    JSch jsch = new JSch();
	    Session session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
	    session.setPassword(config.getPassword());
	    session.setConfig("StrictHostKeyChecking", "no");
	    session.connect();

	    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
	    sftp.connect();

	    List<ChannelSftp.LsEntry> files = new ArrayList<>();
	    sftp.ls(config.getRemoteDirectory()).forEach(obj -> files.add((ChannelSftp.LsEntry) obj));

	    File decryptedFolder = new File(decryptedDir);
	    if (!decryptedFolder.exists()) decryptedFolder.mkdirs();

	    for (ChannelSftp.LsEntry entry : files) {
	        String fileName = entry.getFilename();
	        if (entry.getAttrs().isDir() || fileName.startsWith(".")) continue;

	        boolean matchesDate = dateStrings.stream().anyMatch(fileName::contains);
	        if (!matchesDate) continue;

	        if (isFileAlreadyDownloaded(fileName)) {
	            log.info("Skipping already downloaded file: {}", fileName);
	            continue;
	        }

	        File localFile = new File(downloadDir, fileName);
	        try (FileOutputStream fos = new FileOutputStream(localFile)) {
	            sftp.get(config.getRemoteDirectory() + "/" + fileName, fos);
	        }
	        log.info("⬇️ File downloaded from SFTP: {}", fileName);

	        String decryptedFileName = fileName.endsWith(".pgp")
	                ? fileName.substring(0, fileName.length() - 4)
	                : fileName;

	        File decryptedFile = new File(decryptedFolder, decryptedFileName);
	        try (InputStream keyStream = new ClassPathResource(privateKeyPath).getInputStream()) {
	            pgpEncryptionService.decryptACHFile(localFile, decryptedFile, keyStream, passphrase);
	            if (decryptedFile.length() > 0) {
	                log.info("✅ Successfully decrypted: {}", decryptedFile.getAbsolutePath());
	                service.logEvent(decryptedFile.getName(), "Ready to Process", "SUCCESS");
	                markFileAsDownloaded(fileName);

	                decryptedThisRun.add(decryptedFile);   // <--- ONLY add when success
	            } else {
	                log.warn("⚠️ Decrypted file empty, skipping: {}", decryptedFile.getName());
	                decryptedFile.delete();
	            }
	        } catch (Exception e) {
	            log.error("❌ Failed to decrypt file {}: {}", fileName, e.getMessage());
	        }
	    }

	    sftp.disconnect();
	    session.disconnect();

	    if (decryptedThisRun.isEmpty()) {
	        log.info("⚠️ No files found on SFTP for given date range");
	    }

	    return decryptedThisRun;   
	}



   private boolean isFileAlreadyDownloaded(String fileName) {
    return remoteFileRepository.existsByFileNameAndDownloadStatus(fileName, "DOWNLOADED");
    }

	private void markFileAsDownloaded(String fileName) {
	    Optional<RemoteFile> existingRecord = remoteFileRepository.findByFileName(fileName);
	    RemoteFile record = existingRecord.orElse(new RemoteFile());
	    record.setFileName(fileName);
	    record.setDownloadStatus("DOWNLOADED");
	    record.setDownloadTimestamp(LocalDateTime.now());
	    remoteFileRepository.save(record);
	}
}