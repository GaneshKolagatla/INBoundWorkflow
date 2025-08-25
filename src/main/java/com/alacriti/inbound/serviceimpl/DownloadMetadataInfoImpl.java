package com.alacriti.inbound.serviceimpl;

import com.alacriti.inbound.model.SftpServerCredentials;
import com.alacriti.inbound.service.IDownloadMetadataInfo;

public class DownloadMetadataInfoImpl implements IDownloadMetadataInfo {
	
	    private final String downloadDir;
	    private final String decryptDir;
	    private final String privateKeyDir;
	    private final String passphrase;
	    private final SftpServerCredentials credentialObj;
	    

	    // Constructor (Immutable style)
	    public DownloadMetadataInfoImpl(String downloadDir,
	                            String decryptDir,
	                            String privateKeyDir,
	                            String passphrase,
	                            SftpServerCredentials obj) {
	        this.downloadDir = downloadDir;
	        this.decryptDir = decryptDir;
	        this.privateKeyDir = privateKeyDir;
	        this.passphrase = passphrase;
	        this.credentialObj = obj;
	    }
	    
	@Override
	public String getDownloadDir() {
		// TODO Auto-generated method stub
		return this.downloadDir;
	}

	@Override
	public String getDecryptDir() {
		// TODO Auto-generated method stub
		return this.decryptDir;
	}

	public String getPassPhrase() {
		// TODO Auto-generated method stub
		return this.passphrase;
	}
	
	@Override
	public String getPrivateKeyDir() {
		// TODO Auto-generated method stub
		return this.privateKeyDir;
	}
	
	@Override
	public SftpServerCredentials getSftpCredentialObj() {
		// TODO Auto-generated method stub
		return this.credentialObj;
	}

}
