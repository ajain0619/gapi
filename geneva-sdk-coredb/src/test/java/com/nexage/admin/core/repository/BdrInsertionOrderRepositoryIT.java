package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.type.BDRInsertionOrderType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-insertion-order-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrInsertionOrderRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BdrInsertionOrderRepository bdrInsertionOrderRepository;
  @Autowired private BDRAdvertiserRepository bdrAdvertiserRepository;
  @Autowired private EntityManager entityManager;
  private static final long COMPANY_PID = 1L;

  @Test
  void shouldCreateBDRInsertionOrder() {
    // given
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository
            .findById(2L)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser not found in DB"));

    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    insertionOrder.setName("BDRInsertionOrder");
    insertionOrder.setAdvertiser(advertiser);
    insertionOrder.setComments("comments");
    insertionOrder.setAdomain("domainName");
    insertionOrder.setRefNumber("refNumber");
    insertionOrder.setType(BDRInsertionOrderType.NEXAGE);

    // when
    BdrInsertionOrder result = bdrInsertionOrderRepository.save(insertionOrder);

    // then
    assertNotNull(result.getPid());
    assertEquals(insertionOrder, result);
  }

  @Test
  void shouldUpdateBDRInsertionOrder() {
    // given
    String nameUpdated = "name-updated";
    long insertionOrderPid = 1L;
    long advertiserPid = 2L;
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository
            .findById(advertiserPid)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser not found in DB"));

    BdrInsertionOrder insertionOrder =
        bdrInsertionOrderRepository
            .findById(insertionOrderPid)
            .orElseThrow(() -> new EntityNotFoundException("Insertion Order not found in DB"));
    insertionOrder.setName(nameUpdated);
    insertionOrder.setAdvertiser(advertiser);

    // when
    BdrInsertionOrder result = bdrInsertionOrderRepository.save(insertionOrder);
    entityManager.flush();
    entityManager.refresh(result);

    // then
    assertNotNull(result.getPid());
    assertEquals(insertionOrder, result);
    assertEquals(insertionOrderPid, result.getPid());
    assertEquals(nameUpdated, result.getName());
    assertEquals(advertiserPid, result.getAdvertiserPid());
    assertEquals(advertiserPid, result.getAdvertiser().getPid());
    assertEquals(1, insertionOrder.getVersion());
  }

  @Test
  void shouldGetAllInsertionOrdersForCompany() {
    // when
    List<BdrInsertionOrder> insertionOrders =
        bdrInsertionOrderRepository.findAllByAdvertiser_Company_Pid(COMPANY_PID);

    // then
    assertEquals(2, insertionOrders.size());
    insertionOrders.forEach(
        io -> assertEquals(COMPANY_PID, io.getAdvertiser().getCompany().getPid()));
    insertionOrders.forEach(io -> assertEquals(COMPANY_PID, io.getAdvertiser().getCompanyPid()));
  }

  @Test
  void shouldGetAllCompanyPidsForInsertionOrders() {
    // given
    long[] insertionOrderPids = {1, 2, 3};

    // when
    List<Long> companyPids =
        bdrInsertionOrderRepository.findAllCompanyPidsForInsertionOrders(insertionOrderPids);

    // then
    assertEquals(2, companyPids.size());
    assertEquals(List.of(1L, 2L), companyPids);
  }

  @Test
  void shouldFindAll() {
    // when
    List<BdrInsertionOrder> bdrInsertionOrders = bdrInsertionOrderRepository.findAll();

    // then
    assertEquals(
        Set.of(1L, 2L, 3L, 4L),
        bdrInsertionOrders.stream().map(BdrInsertionOrder::getPid).collect(Collectors.toSet()));
  }
}
