package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealView;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectDealRepository
    extends JpaRepository<DirectDeal, Long>, JpaSpecificationExecutor<DirectDeal> {

  Optional<DealView> findByPid(Long pid);

  /**
   * Check if a deal pid exists
   *
   * @param pid {@link Long}
   * @return {@link boolean}
   */
  boolean existsByPid(Long pid);

  /**
   * @param dealIds list of deal ids
   * @return list of deals
   */
  List<DirectDeal> findByDealIdIn(Collection<String> dealIds);

  Optional<DirectDeal> findByDealId(String dealId);

  int countByDealId(String dealID);

  int countByPidIn(Set<Long> pids);

  List<DirectDeal> findByRulesNotNull();

  @Query(
      "SELECT d.pid FROM DirectDeal d WHERE d.status > 0 AND d.placementFormula IS NOT NULL AND d.autoUpdate = TRUE")
  List<Long> findActiveDealsWithAutoUpdateFormula();

  @Query("SELECT d.placementFormulaStatus FROM DirectDeal d WHERE d.pid = :pid")
  PlacementFormulaStatus findPlacementFormulaStatusByPid(@Param("pid") Long pid);
}
