package com.alacriti.inbound.service.workflow;

import com.alacriti.inbound.service.IBatchDataPostProcessor;
import com.alacriti.inbound.service.IBatchDataPreProcessor;
import com.alacriti.inbound.service.IBatchDataProcessor;
import com.alacriti.inbound.service.IBatchDataReader;
import com.alacriti.inbound.service.IBatchDataValidator;

import lombok.Data;

@Data
public class BatchWorkFlow {

    private final IBatchDataReader fileReader;
    private final IBatchDataValidator fileValidator;
    private final IBatchDataPreProcessor filePreProcessor;
    private final IBatchDataProcessor fileProcessor;
    private final IBatchDataPostProcessor filePostProcessor;

    private BatchWorkFlow(Builder builder) {

        this.fileReader = builder.fileReader;
        this.fileValidator = builder.fileValidator;
        this.filePreProcessor=builder.filePreProcessor;
        this.fileProcessor = builder.fileProcessor;
        this.filePostProcessor=builder.filePostProcessor;
    }

   

    public static class Builder {

        private IBatchDataReader fileReader;
        private IBatchDataValidator fileValidator;
        private IBatchDataPreProcessor filePreProcessor;
        private IBatchDataProcessor fileProcessor;
        private IBatchDataPostProcessor filePostProcessor;

        
        



        public Builder withFileReader(IBatchDataReader fileReader) {
            this.fileReader = fileReader;
            return this;
        }

        public Builder withFileValidator(IBatchDataValidator fileValidator) {
            this.fileValidator = fileValidator;
            return this;
        }
        
        public Builder withFilePreProcessor(IBatchDataPreProcessor filePreProcessor) {
        	this.filePreProcessor=filePreProcessor;
        	return this;
        }

        public Builder withFileProcessor(IBatchDataProcessor fileProcessor) {
            this.fileProcessor = fileProcessor;
            return this;
        }
        
        public Builder withFilePostProcessor(IBatchDataPostProcessor filePostProcessor) {
        	this.filePostProcessor=filePostProcessor;
        	return this;
        }

        public BatchWorkFlow build() {
            return new BatchWorkFlow(this);
        }
    }
}
