package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrTargetGroupRepository extends JpaRepository<BdrTargetGroup, Long> {
  @Override
  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"lineItem"})
  List<BdrTargetGroup> findAll();
}
