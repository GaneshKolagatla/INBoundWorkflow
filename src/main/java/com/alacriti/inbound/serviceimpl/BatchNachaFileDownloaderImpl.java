package com.alacriti.inbound.serviceimpl;

import java.io.File;
import java.util.List;

import com.alacriti.inbound.service.IBatchNachaFileDownloader;
import com.alacriti.inbound.service.IDownloadMetadataInfo;

public class BatchNachaFileDownloaderImpl implements IBatchNachaFileDownloader<IDownloadMetadataInfo> {

	@Override
	public List<File> download(IDownloadMetadataInfo metadata) {
		return null;
	}

}
