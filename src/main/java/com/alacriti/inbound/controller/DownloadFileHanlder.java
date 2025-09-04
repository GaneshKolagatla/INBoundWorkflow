package com.alacriti.inbound.controller;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alacriti.inbound.dto.DownloadDTO;
import com.alacriti.inbound.model.SftpServerCredentials;
import com.alacriti.inbound.repository.SftpServerRepo;
import com.alacriti.inbound.service.IBatchNachaFileDownloader;
import com.alacriti.inbound.service.IDownloadMetadataInfo;
import com.alacriti.inbound.service.IWorkflowExecutor;
import com.alacriti.inbound.serviceimpl.DownloadMetadataInfoImpl;

@RestController
public class DownloadFileHanlder {

	

	@Autowired
	IBatchNachaFileDownloader<IDownloadMetadataInfo> fileDownloader;

	@Autowired
	SftpServerRepo repo;

	@Autowired
	IWorkflowExecutor executor;

	private static final String PRIVATE_KEY_PATH = "keys/private_key.asc";
	private static final String DOWNLOAD_DIR = "target/download-ach";
	private static final String DECRYPTED_DIR = "target/decrypted-ach";
	private static final String PASSPHRASE = "8823027374";


	@PostMapping("/download")
	public ResponseEntity<?> downloadFiles(@RequestBody DownloadDTO dto, 
	                                       @RequestParam(defaultValue = "1") int days) throws Exception {
	    Optional<SftpServerCredentials> sftpServerObj = repo.findById(dto.getClient_key());
	    if (sftpServerObj.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client key not found.");
	    }

	    if (days <= 0) {
	        return ResponseEntity.badRequest().body("Days parameter must be greater than 0.");
	    }

	    SftpServerCredentials credentialsObj = sftpServerObj.get();
	    IDownloadMetadataInfo info = new DownloadMetadataInfoImpl(DOWNLOAD_DIR, DECRYPTED_DIR, PRIVATE_KEY_PATH,
	            PASSPHRASE, credentialsObj);

	    // Pass 'days' parameter to the download service
	    List<File> downloadedFiles = fileDownloader.download(info, days);

	    return ResponseEntity.ok().body("Downloaded files count: " + downloadedFiles.size());
	}


}
