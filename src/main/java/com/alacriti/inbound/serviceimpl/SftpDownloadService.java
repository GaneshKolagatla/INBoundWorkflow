package com.alacriti.inbound.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alacriti.inbound.model.RemoteFileEvent;
import com.alacriti.inbound.model.SftpServerCredentials;
import com.alacriti.inbound.repository.RemoteFileEventRepository;
import com.alacriti.inbound.service.IFileEventLogService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class SftpDownloadService {

	private final PGPDecryptionService pgpEncryptionService;

	@Autowired
	private IFileEventLogService service; // old log system (keep for backward compatibility)

	@Autowired
	private RemoteFileEventRepository remoteFileEventRepository; // DB repo for remote_file_tbl

	public SftpDownloadService(PGPDecryptionService pgpEncryptionService) {
		this.pgpEncryptionService = pgpEncryptionService;
	}

	public void downloadAndDecryptByDate(SftpServerCredentials config, String date, String downloadDir,
			String decryptedDir, String privateKeyPath, String passphrase) throws Exception {

		List<File> downloadedFiles = downloadFilesFromSftp(config, date, downloadDir);

		if (downloadedFiles.isEmpty()) {
			return;
		}

		File decryptedFolder = new File(decryptedDir);
		if (!decryptedFolder.exists())
			decryptedFolder.mkdirs();

		for (File encryptedFile : downloadedFiles) {
			String decryptedFileName = encryptedFile.getName().endsWith(".pgp")
					? encryptedFile.getName().substring(0, encryptedFile.getName().length() - 4)
					: encryptedFile.getName();

			File decryptedFile = new File(decryptedFolder, decryptedFileName);

			try (InputStream keyStream = new ClassPathResource(privateKeyPath).getInputStream()) {
				pgpEncryptionService.decryptACHFile(encryptedFile, decryptedFile, keyStream, passphrase);

				if (decryptedFile.length() > 0) {
					service.logEvent(decryptedFile.getName(), "Ready to Process", "SUCCESS"); // old log
					saveRemoteFileEvent(config.getClientKey(), decryptedFile.getName(), "DECRYPT",
							"DEC" + System.currentTimeMillis(), "SUCCESS", "Decrypted successfully");
				} else {
					decryptedFile.delete();
					saveRemoteFileEvent(config.getClientKey(), decryptedFile.getName(), "DECRYPT",
							"DEC" + System.currentTimeMillis(), "FAILED", "Decrypted file was empty");
				}
			} catch (Exception e) {
				saveRemoteFileEvent(config.getClientKey(), decryptedFile.getName(), "DECRYPT",
						"DEC" + System.currentTimeMillis(), "FAILED", e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<File> downloadFilesFromSftp(SftpServerCredentials config, String date, String downloadDir)
			throws Exception {
		List<File> downloadedFiles = new ArrayList<>();

		JSch jsch = new JSch();
		Session session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
		session.setPassword(config.getPassword());
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();

		List<ChannelSftp.LsEntry> files = new ArrayList<>();
		sftp.ls(config.getRemoteDirectory()).forEach(obj -> files.add((ChannelSftp.LsEntry) obj));

		for (ChannelSftp.LsEntry entry : files) {
			String fileName = entry.getFilename();

			if (entry.getAttrs().isDir() || fileName.startsWith("."))
				continue;
			if (!fileName.contains(date))
				continue;

			try {
				File localFile = new File(downloadDir, fileName);
				try (FileOutputStream fos = new FileOutputStream(localFile)) {
					sftp.get(config.getRemoteDirectory() + "/" + fileName, fos);
				}

				downloadedFiles.add(localFile);

				// Log both DOWNLOAD (legacy) and PULL (new)
				saveRemoteFileEvent(config.getClientKey(), fileName, "DOWNLOAD", "D" + System.currentTimeMillis(),
						"SUCCESS", "Downloaded successfully (legacy)");
				saveRemoteFileEvent(config.getClientKey(), fileName, "PULL", "PULL" + System.currentTimeMillis(),
						"SUCCESS", "Pulled from SFTP to local successfully");

			} catch (Exception ex) {
				saveRemoteFileEvent(config.getClientKey(), fileName, "PULL", "PULL" + System.currentTimeMillis(),
						"FAILED", ex.getMessage());
				throw ex;
			}
		}

		sftp.disconnect();
		session.disconnect();

		return downloadedFiles;
	}

	// 🔹 You can reuse this same method from an UploadService to log PUSH events
	public void logPushEvent(String clientKey, String fileName, boolean success, String remarks) {
		saveRemoteFileEvent(clientKey, fileName, "PUSH", "PUSH" + System.currentTimeMillis(),
				success ? "SUCCESS" : "FAILED", remarks);
	}

	// helper method to save events
	private void saveRemoteFileEvent(String clientKey, String fileName, String eventType, String sequenceNo,
			String status, String remarks) {
		RemoteFileEvent event = new RemoteFileEvent();
		event.setClientKey(clientKey);
		event.setFileName(fileName);
		event.setEventType(eventType); // DOWNLOAD / PULL / PUSH / DECRYPT
		event.setSequenceNo(sequenceNo);
		event.setStatus(status);
		event.setEventTime(LocalDateTime.now());
		event.setRemarks(remarks);

		remoteFileEventRepository.save(event);
	}
}
