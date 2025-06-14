package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleDSPBiddersView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDSPRepository
    extends JpaRepository<RuleDSPBiddersView, Long>, JpaSpecificationExecutor<RuleDSPBiddersView> {

  @Query(
      "SELECT DISTINCT bc FROM RuleDSPBiddersView bc INNER JOIN FETCH bc.bidders b WHERE b.trafficStatus = true")
  List<RuleDSPBiddersView> findDSPsWithActiveBidders();
}
