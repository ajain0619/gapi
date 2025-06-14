package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface BdrLineItemRepository extends JpaRepository<BDRLineItem, Long> {

  @Override
  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"creative", "insertionOrder"})
  List<BDRLineItem> findAll();
}
