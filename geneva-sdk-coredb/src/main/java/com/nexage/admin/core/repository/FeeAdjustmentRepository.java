package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeAdjustmentRepository
    extends JpaRepository<FeeAdjustment, Long>, JpaSpecificationExecutor<FeeAdjustment> {

  @EntityGraph(attributePaths = {"feeAdjustmentBuyers.buyer"})
  Page<FeeAdjustment> findAll(
      Specification<FeeAdjustment> feeAdjustmentSpecification, Pageable pageable);
}
