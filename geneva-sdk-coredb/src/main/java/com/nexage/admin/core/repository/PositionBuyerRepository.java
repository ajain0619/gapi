package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.PositionBuyer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionBuyerRepository
    extends JpaRepository<PositionBuyer, Long>, JpaSpecificationExecutor<PositionBuyer> {

  Optional<PositionBuyer> findByPositionPid(Long positionPid);
}
