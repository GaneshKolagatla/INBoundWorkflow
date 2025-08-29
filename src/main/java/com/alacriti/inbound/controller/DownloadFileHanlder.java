package com.alacriti.inbound.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	//    @Autowired
	//    IDownloadMetadataInfo info;

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
	public void downloadFile(@RequestBody DownloadDTO dto) throws Exception {
		Optional<SftpServerCredentials> sftpServerObj = repo.findById(dto.getClient_key());
		SftpServerCredentials credentialsObj = sftpServerObj.get();
		IDownloadMetadataInfo info = new DownloadMetadataInfoImpl(DOWNLOAD_DIR, DECRYPTED_DIR, PRIVATE_KEY_PATH,
				PASSPHRASE, credentialsObj);
		fileDownloader.download(info);

	}
}
