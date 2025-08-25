package com.alacriti.inbound.service;

import java.io.File;
import java.util.List;

public interface IBatchNachaFileDownloader<T extends IDownloadMetadataInfo> {
	List<File> download(T metadata);
}
