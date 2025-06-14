package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BdrConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrConfigRepository extends JpaRepository<BdrConfig, Long> {
  BdrConfig findByProperty(String property);
}
