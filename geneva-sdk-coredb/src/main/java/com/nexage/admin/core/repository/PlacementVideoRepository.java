package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementVideoRepository
    extends JpaRepository<PlacementVideo, Long>, JpaSpecificationExecutor<PlacementVideo> {

  /**
   * Check whether an a{@link PlacementVideo} exists by pid.
   *
   * @param placementVideoPid: pid of the placementVideo to query
   * @return boolean, true if it exists ， else false
   */
  @Query(
      "SELECT CASE WHEN COUNT(p)> 0 THEN true ELSE false END FROM PlacementVideo p WHERE p.pid = :placementVideoPid")
  boolean existsByPid(@Param("placementVideoPid") Long placementVideoPid);
}
