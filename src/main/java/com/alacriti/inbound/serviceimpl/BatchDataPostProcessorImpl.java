package com.alacriti.inbound.serviceimpl;



import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alacriti.inbound.service.IBatchDataPostProcessor;

@Service
public class BatchDataPostProcessorImpl implements IBatchDataPostProcessor {

	@Override
	public void postProcess() {
		try {
	        String xmlPayload = """
	            <PaymentInstruction>
	                <fileName>%s</fileName>
	                <createdDate>%s</createdDate>
	                <clientKey>%s</clientKey>
	            </PaymentInstruction>
	            """.formatted(fileName, createdDate, clientKey);

	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_XML);

	        HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);
	        String endpoint = "https://xyz.ngrok.io/send";
	        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
	        System.out.println("Response: " + response.getBody());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	}

}
