package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BdrConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrConfigRepositoryIT extends CoreDbSdkIntegrationTestBase {

  public static final String MODIFIER_STRING = "#";

  @Autowired protected BdrConfigRepository bdrConfigRepository;

  @Test
  void shouldCreateBdrConfig() {
    // given
    BdrConfig object = new BdrConfig();
    object.setProperty("BDRConfig0");
    object.setValue("value00");
    object.setDescription("Desc of " + "BDRConfig0");

    // when
    BdrConfig result = bdrConfigRepository.save(object);

    // then
    assertNotNull(result.getPid());
    assertEquals(object, result);
  }

  @Test
  void shouldFindAll() throws Exception {
    // when
    List<BdrConfig> objects = bdrConfigRepository.findAll();

    // then
    assertEquals(5, objects.size());
  }

  @Test
  void shouldUpdateBdrConfig() throws Exception {
    // given
    BdrConfig object = bdrConfigRepository.findAll().get(0);
    object.setValue(object.getValue() + MODIFIER_STRING);
    object.setProperty(object.getProperty() + MODIFIER_STRING);
    object.setDescription(object.getDescription() + MODIFIER_STRING);

    // when
    BdrConfig result = bdrConfigRepository.save(object);

    // then
    assertEquals(object, result);
  }
}
