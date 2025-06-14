package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealBidderConfigView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DealBidderRepository
    extends JpaRepository<DealBidderConfigView, Long>,
        JpaSpecificationExecutor<DealBidderConfigView> {}
