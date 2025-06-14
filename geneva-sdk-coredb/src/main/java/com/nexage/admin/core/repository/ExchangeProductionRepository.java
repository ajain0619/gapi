package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ExchangeProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeProductionRepository extends JpaRepository<ExchangeProduction, Integer> {}
