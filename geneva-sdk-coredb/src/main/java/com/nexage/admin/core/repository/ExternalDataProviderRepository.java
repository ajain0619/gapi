package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ExternalDataProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalDataProviderRepository extends JpaRepository<ExternalDataProvider, Long> {}
