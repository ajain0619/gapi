package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BdrCreative;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrCreativeRepository extends JpaRepository<BdrCreative, Long> {

  Optional<BdrCreative> findByPidAndAdvertiser_Pid(Long pid, Long advertiserPid);

  @Query(
      "SELECT creative FROM BdrCreative creative WHERE pid in :creativesPids and creative.advertiser.pid = :advertiserPid and creative.status = 1")
  Set<BdrCreative> findActiveByAdvertiserPidAndPidInCreativePids(
      @Param("advertiserPid") long advertiserPid, @Param("creativesPids") Set<Long> creativesPids);

  @Query(
      "SELECT creative FROM BdrCreative creative WHERE creative.advertiser.pid = :advertiserPid and creative.status = 1")
  Set<BdrCreative> findActiveByAdvertiserPid(@Param("advertiserPid") long advertiserPid);

  boolean existsByNameAndAdvertiserCompanyPid(String name, long pid);

  @Override
  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"advertiser"})
  List<BdrCreative> findAll();
}
