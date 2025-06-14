package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ExchangeRegional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRegionalRepository extends JpaRepository<ExchangeRegional, Long> {}
