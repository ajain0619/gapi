package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.PositionBuyer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/position-buyer-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class PositionBuyerRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired public PositionBuyerRepository positionBuyerRepository;

  @Test
  void testFindByPositionPid() {
    Long positionPid = 4L;
    String buyerPositionId = "5662618";
    PositionBuyer p2 = positionBuyerRepository.findByPositionPid(positionPid).get();
    assertEquals(p2.getBuyerPositionId(), buyerPositionId);
  }
}
