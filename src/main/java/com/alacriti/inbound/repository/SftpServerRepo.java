package com.alacriti.inbound.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alacriti.inbound.model.SftpServerCredentials;

public interface SftpServerRepo extends JpaRepository<SftpServerCredentials, String> {
	Optional<SftpServerCredentials> findByClientKey(String clientKey);
}