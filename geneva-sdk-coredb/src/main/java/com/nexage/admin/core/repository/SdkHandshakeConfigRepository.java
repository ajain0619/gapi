package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.SDKHandshakeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SdkHandshakeConfigRepository
    extends JpaRepository<SDKHandshakeConfiguration, Long> {

  boolean existsByHandshakeKey(String handshakeKey);
}
