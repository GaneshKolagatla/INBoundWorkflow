package com.alacriti.inbound.serviceimpl;



import java.io.File;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.alacriti.inbound.component.PGPDecryptor;

@Service
public class PGPDecryptionService {

	public File decryptACHFile(File encryptedFile, File outputFile, InputStream privateKeyStream, String passphrase)
			throws Exception {
		PGPDecryptor decryptor = new PGPDecryptor();
		decryptor.decryptSingleFile(encryptedFile, outputFile, privateKeyStream, passphrase);
		return outputFile;
	}
	
}