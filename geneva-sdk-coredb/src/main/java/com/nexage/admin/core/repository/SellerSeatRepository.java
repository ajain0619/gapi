package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.SellerSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerSeatRepository
    extends JpaRepository<SellerSeat, Long>, JpaSpecificationExecutor<SellerSeat> {}
