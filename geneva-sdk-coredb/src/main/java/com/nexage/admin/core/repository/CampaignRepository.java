package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Target;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository
    extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

  List<Campaign> findByTargets_TypeAndTargets_FilterLike(Target.TargetType type, String filter);

  List<Campaign> findBySellerId(Long sellerId);

  boolean existsByPidAndSellerId(Long pid, Long sellerId);
}
