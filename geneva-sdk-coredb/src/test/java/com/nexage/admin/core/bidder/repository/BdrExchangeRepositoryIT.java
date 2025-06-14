package com.nexage.admin.core.bidder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.admin.core.bidder.type.BDRStatus;
import com.nexage.admin.core.repository.BdrExchangeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-exchange-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrExchangeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired BdrExchangeRepository bdrExchangeRepository;

  public static final String MODIFIER_STRING = "#";
  public static final Long MODIFIER_LONG = 100L;
  public static final Integer MODIFIER_INTEGER = 10;

  private BDRExchange buildBDRExchange(
      String name, BDRStatus status, Long nexageId, Integer version) {
    BDRExchange object = new BDRExchange();
    object.setName(name);
    object.setExternalId("externalId." + name);
    object.setHndler("hndlr." + name);
    object.setStatus(status);
    object.setNexageId(nexageId);
    object.setVersion(version);
    return object;
  }

  @Test
  void shouldCreateNewBdrExchange() {
    BDRExchange object = buildBDRExchange("BDRExchange0", BDRStatus.ACTIVE, 100L, 0);
    BDRExchange result = bdrExchangeRepository.save(object);
    assertNotNull(result.getPid());
    assertEquals(object, result);
  }

  @Test
  void shouldFindAllBdrExchanges() {
    List<BDRExchange> objects = bdrExchangeRepository.findAll();
    assertEquals(3, objects.size());
  }

  @Test
  void shouldUpdateExistingBdrExchange() {
    BDRExchange object = bdrExchangeRepository.findAll().get(0);
    object.setExternalId(object.getExternalId() + MODIFIER_STRING);
    object.setHndler(object.getHndler() + MODIFIER_STRING);
    object.setName(object.getName() + MODIFIER_STRING);
    object.setStatus(BDRStatus.INACTIVE);
    object.setNexageId(object.getNexageId() + MODIFIER_LONG);
    object.setVersion(object.getVersion() + MODIFIER_INTEGER);
    bdrExchangeRepository.save(object);
    BDRExchange result = bdrExchangeRepository.findById(object.getPid()).orElse(null);
    assertEquals(object, result);
  }
}
