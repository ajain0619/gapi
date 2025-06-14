package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.SellerSeatRule;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerSeatRuleRepository
    extends JpaRepository<SellerSeatRule, Long>, JpaSpecificationExecutor<SellerSeatRule> {

  /**
   * Soft-deletes a requested rule by setting {@link SellerSeatRule#status} to {@link
   * com.nexage.admin.core.enums.Status#DELETED}
   */
  @Query("UPDATE SellerSeatRule SET status = -1 WHERE pid=?1")
  @Modifying
  @Transactional
  void delete(Long rulePid);

  /**
   * Soft-deletes a requested rule by setting sellerSeatRule to {@link
   * com.nexage.admin.core.enums.Status#DELETED}
   */
  @Override
  @Query("UPDATE SellerSeatRule ssr SET ssr.status = -1 WHERE ssr=?1")
  @Modifying
  @Transactional
  void delete(SellerSeatRule sellerSeatRule);
}
