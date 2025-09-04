package com.alacriti.inbound.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.RemoteFile;

public interface RemoteFileRepository extends JpaRepository<RemoteFile, Long> {
    boolean existsByFileNameAndDownloadStatus(String fileName, String downloadStatus);

    Optional<RemoteFile> findByFileName(String fileName);
}