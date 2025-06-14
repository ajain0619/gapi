package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrInsertionOrderRepository extends JpaRepository<BdrInsertionOrder, Long> {

  List<BdrInsertionOrder> findAllByAdvertiser_Company_Pid(long companyPid);

  @Query(
      "SELECT distinct io.advertiser.company.pid FROM BdrInsertionOrder io WHERE io.pid in (:pids)")
  List<Long> findAllCompanyPidsForInsertionOrders(@Param("pids") long[] insertionOrderPids);

  @Override
  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"advertiser"})
  List<BdrInsertionOrder> findAll();
}
