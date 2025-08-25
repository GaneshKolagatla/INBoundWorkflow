package com.alacriti.inbound.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alacriti.inbound.service.IBatchDataPostProcessor;
import com.alacriti.inbound.service.IBatchDataPreProcessor;
import com.alacriti.inbound.service.IBatchDataProcessor;
import com.alacriti.inbound.service.IBatchDataReader;
import com.alacriti.inbound.service.IBatchDataValidator;
import com.alacriti.inbound.service.workflow.BatchWorkFlow;

@Configuration
public class WorkflowConfig {



    @Autowired
    private IBatchDataReader fileReader;

    @Autowired
    private IBatchDataValidator fileValidator;
    
    @Autowired
    private IBatchDataPreProcessor filePreProcessor;

    @Autowired
    private IBatchDataProcessor fileProcessor;
    
    @Autowired
    private IBatchDataPostProcessor filePostProcessor;

    @Bean
    BatchWorkFlow inBoundWorkFlow() {
        return new BatchWorkFlow.Builder()
                .withFileReader(fileReader)
                .withFileValidator(fileValidator)
                .withFilePreProcessor(filePreProcessor)
                .withFileProcessor(fileProcessor)
                .withFilePostProcessor(filePostProcessor)
                .build();
    }
}
