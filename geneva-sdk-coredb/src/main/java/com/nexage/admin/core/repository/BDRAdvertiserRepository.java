package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BDRAdvertiserRepository extends JpaRepository<BDRAdvertiser, Long> {

  BDRAdvertiser findByNameAndCompanyPid(String name, long companyPid);

  List<BDRAdvertiser> findByCompanyPid(long companyPid);

  @Override
  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"company"})
  List<BDRAdvertiser> findAll();
}
