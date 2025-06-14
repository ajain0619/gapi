package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.AdserverConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdserverConfigurationRepository
    extends JpaRepository<AdserverConfiguration, Long> {
  AdserverConfiguration findByProperty(String property);
}
