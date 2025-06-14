package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementVideoCompanionRepository
    extends JpaRepository<PlacementVideoCompanion, Long>,
        JpaSpecificationExecutor<PlacementVideoCompanion> {
  Page<PlacementVideoCompanion> findByPlacementVideoPid(Long placementVideoPid, Pageable pageable);

  @Modifying
  @Query(
      "DELETE FROM PlacementVideoCompanion WHERE placementVideoPid=:placementVideoPid AND pid NOT IN (:dtoPids)")
  void delete(
      @Param("placementVideoPid") Long placementVideoPid,
      @Param("dtoPids") Set<Long> placementVideoDTOCompanionPids);

  @Modifying
  @Query("DELETE FROM PlacementVideoCompanion WHERE placementVideoPid=:placementVideoPid")
  void delete(@Param("placementVideoPid") Long placementVideoPid);
}
