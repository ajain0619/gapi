package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ExchangeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeConfigRepository extends JpaRepository<ExchangeConfig, Long> {

  ExchangeConfig findByProperty(String property);
}
