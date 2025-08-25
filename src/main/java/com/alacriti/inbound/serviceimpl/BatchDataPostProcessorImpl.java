package com.alacriti.inbound.serviceimpl;



import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.alacriti.inbound.service.IBatchDataPostProcessor;

@Service
public class BatchDataPostProcessorImpl implements IBatchDataPostProcessor {

	@Override
	@PostMapping("/hitkafka")
	public void postProcess() {
		try {
	        String xmlPayload = """
	           <FileNotification>
                 <fileName>monthly_report_august.pdf</fileName>
                 <clientKey>ICICI001</clientKey>
                 <date>2025-08-25</date>
               </FileNotification>
	            """.formatted();

	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_XML);

	        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);
	        String endpoint = "https://7ea3d690c952.ngrok-free.app/send-file-notification";
	        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
	        System.out.println("Response: " + response.getBody());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	}

}
