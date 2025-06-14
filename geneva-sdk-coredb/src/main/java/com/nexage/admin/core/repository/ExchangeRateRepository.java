package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.model.ExchangeRatePrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository
    extends JpaRepository<ExchangeRate, ExchangeRatePrimaryKey>,
        JpaSpecificationExecutor<ExchangeRate> {}
