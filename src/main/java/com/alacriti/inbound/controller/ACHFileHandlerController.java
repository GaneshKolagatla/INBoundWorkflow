package com.alacriti.inbound.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alacriti.inbound.service.IBatchNachaFileDownloader;
import com.alacriti.inbound.service.IDownloadMetadataInfo;

@RestController
public class ACHFileHandlerController {

    @Autowired
    IDownloadMetadataInfo info;

    @Autowired
    IBatchNachaFileDownloader<IDownloadMetadataInfo> fileDownloader;

    @PostMapping("/download")
    public void downloadFile() {
        fileDownloader.download(info);
    }
}