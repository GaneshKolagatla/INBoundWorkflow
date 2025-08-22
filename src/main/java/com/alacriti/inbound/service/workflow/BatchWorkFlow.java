package com.alacriti.inbound.service.workflow;

import com.alacriti.inbound.service.IBatchDataProcessor;
import com.alacriti.inbound.service.IBatchDataReader;
import com.alacriti.inbound.service.IBatchDataValidator;
import com.alacriti.inbound.service.IFileDecryptor;
import com.alacriti.inbound.service.IFileDownloader;

public class BatchWorkFlow {
    private final IFileDownloader fileDownloader;
    private final IFileDecryptor fileDecryptor;
    private final IBatchDataReader fileReader;
    private final IBatchDataValidator fileValidator;
    private final IBatchDataProcessor fileProcessor;

    private BatchWorkFlow(Builder builder) {
        this.fileDownloader = builder.fileDownloader;
        this.fileDecryptor = builder.fileDecryptor;
        this.fileReader = builder.fileReader;
        this.fileValidator = builder.fileValidator;
        this.fileProcessor = builder.fileProcessor;
    }

    public void execute() throws Exception {
       fileDownloader.downloadFile();
       fileDecryptor.decryptFile();
       fileReader.read();
       fileValidator.validate();
       fileProcessor.process();
    }

    public static class Builder {
        private IFileDownloader fileDownloader;
        private IFileDecryptor fileDecryptor;
        private IBatchDataReader fileReader;
        private IBatchDataValidator fileValidator;
        private IBatchDataProcessor fileProcessor;

        public Builder withFileDownloader(IFileDownloader fileDownloader) {
            this.fileDownloader = fileDownloader;
            return this;
        }

        public Builder withFileDecrypt(IFileDecryptor fileDecrypt) {
            this.fileDecryptor = fileDecrypt;
            return this;
        }

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
