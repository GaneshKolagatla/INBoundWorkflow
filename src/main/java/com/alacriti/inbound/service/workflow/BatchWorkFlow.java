package com.alacriti.inbound.service.workflow;

import com.alacriti.inbound.service.IBatchDataProcessor;
import com.alacriti.inbound.service.IBatchDataReader;
import com.alacriti.inbound.service.IBatchDataValidator;

import lombok.Data;

@Data
public class BatchWorkFlow {

    private final IBatchDataReader fileReader;
    private final IBatchDataValidator fileValidator;
    private final IBatchDataProcessor fileProcessor;

    private BatchWorkFlow(Builder builder) {

        this.fileReader = builder.fileReader;
        this.fileValidator = builder.fileValidator;
        this.fileProcessor = builder.fileProcessor;
    }

   

    public static class Builder {

        private IBatchDataReader fileReader;
        private IBatchDataValidator fileValidator;
        private IBatchDataProcessor fileProcessor;



        public Builder withFileReader(IBatchDataReader fileReader) {
            this.fileReader = fileReader;
            return this;
        }

        public Builder withFileValidator(IBatchDataValidator fileValidator) {
            this.fileValidator = fileValidator;
            return this;
        }

        public Builder withFileProcessor(IBatchDataProcessor fileProcessor) {
            this.fileProcessor = fileProcessor;
            return this;
        }

        public BatchWorkFlow build() {
            return new BatchWorkFlow(this);
        }
    }
}
