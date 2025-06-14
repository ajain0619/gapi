package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BDRExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrExchangeRepository extends JpaRepository<BDRExchange, Long> {}
