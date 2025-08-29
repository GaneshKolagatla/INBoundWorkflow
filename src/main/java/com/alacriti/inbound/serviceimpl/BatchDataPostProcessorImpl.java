package com.alacriti.inbound.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alacriti.inbound.service.IBatchDataPostProcessor;
import com.alacriti.inbound.service.IFileEventLogService;
import com.alacriti.inbound.util.ACHFile;

@Service
public class BatchDataPostProcessorImpl implements IBatchDataPostProcessor {

	@Autowired
	IFileEventLogService service;

	public void postProcess(ACHFile file) {
		String rawDate = file.getCreationDate();
		String formattedDate = rawDate.substring(0, 4) + "-" + rawDate.substring(4, 6) + "-" + rawDate.substring(6, 8);

		try {
			String xmlPayload = """
					<FileNotification>
					     <fileName>%s</fileName>
					     <clientKey>ICICI001</clientKey>
					     <date>%s</date>
					   </FileNotification>
					 """.formatted(file.getFileName(), formattedDate);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);

			HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);
			String endpoint = "https://4beb06658aa0.ngrok-free.app/send-file-notification";
			ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
			System.out.println("Response: " + response.getBody());
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

}
