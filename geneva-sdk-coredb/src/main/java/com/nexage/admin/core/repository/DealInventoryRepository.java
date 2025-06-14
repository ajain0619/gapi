package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealInventory;
import com.nexage.admin.core.model.DealInventoryType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealInventoryRepository extends JpaRepository<DealInventory, Long> {

  /**
   * Check if pid and fileName combination exists in database
   *
   * @param pid {@link Long}
   * @param fileName {@link String}
   * @return {@link Boolean}
   */
  boolean existsByPidAndFileName(Long pid, String fileName);

  /**
   * Check if a file exists by Deal Inventory Type and Deal Id
   *
   * @param fileType
   * @return
   */
  @Query(
      "SELECT COUNT(di) > 0 FROM DealInventory di WHERE di.fileId LIKE %:dealId AND di.fileType = :fileType")
  boolean existsByFileTypeAndDealIdAppended(
      @Param("fileType") DealInventoryType fileType, @Param("dealId") Long dealIdAppended);

  /**
   * find DealInventory object by fileId in database
   *
   * @param pid {@link Long}
   * @return {@link DealInventory}
   */
  Optional<DealInventory> findByPid(Long pid);

  /**
   * Delete DealInventory object by specified fileId.
   *
   * @param pid (@link Long}
   */
  void deleteByPid(Long pid);
}
