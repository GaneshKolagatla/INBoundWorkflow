package com.alacriti.inbound.service;

import com.alacriti.inbound.model.SftpServerCredentials;

public interface IDownloadMetadataInfo {
	String getDownloadDir();
	String getDecryptDir();
	String getPassPhrase();
	String getPrivateKeyDir();
	SftpServerCredentials getSftpCredentialObj();
}
