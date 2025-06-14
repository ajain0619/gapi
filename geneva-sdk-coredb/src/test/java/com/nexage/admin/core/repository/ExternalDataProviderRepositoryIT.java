package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BidRequestLocation;
import com.nexage.admin.core.model.ExchangeProduction;
import com.nexage.admin.core.model.ExternalDataProvider;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/external-data-provider-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class ExternalDataProviderRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private ExternalDataProviderRepository externalDataProviderRepository;
  @Autowired private ExchangeProductionRepository exchangeProductionRepository;

  @Test
  void shouldFindExternalDataProviderById() {
    // given
    long pid = 2L;

    // when
    ExternalDataProvider edp = externalDataProviderRepository.findById(pid).orElseThrow();

    // then
    assertEquals(pid, edp.getPid());
  }

  @Test
  void shouldFindAllExternalDataProviders() {
    // when
    List<ExternalDataProvider> externalDataProviders = externalDataProviderRepository.findAll();

    // then
    assertEquals(3, externalDataProviders.size());
  }

  @Test
  void shouldDeleteExternalDataProvider() {
    // given
    long pid = 3L;
    ExternalDataProvider edp = externalDataProviderRepository.findById(pid).orElseThrow();

    // when
    externalDataProviderRepository.delete(edp);

    // then
    assertFalse(externalDataProviderRepository.existsById(pid));
  }

  @Test
  void shouldCreateExternalDataProvider() {
    // given
    ExternalDataProvider edp = new ExternalDataProvider();
    edp.setName("edp-name");
    edp.setBaseUrl("https://new-base-url.com");
    edp.setEnablementStatus(ExternalDataProvider.EnablementStatus.ACTIVE);
    edp.setDescription("edp-description");
    edp.setDataProviderImplClass("edp-impl-class");
    edp.setFilterRequestRate(100);
    edp.setConfiguration("edp-configuration");
    edp.setBidRequestLocation(BidRequestLocation.Device);
    edp.setBidRequestAttributeName("edp-attribute-name");
    edp.setBidderAliasRequired(true);
    edp.setExchanges(Set.of(exchangeProductionRepository.findById(1).orElseThrow()));

    // when
    ExternalDataProvider saved = externalDataProviderRepository.save(edp);

    // then
    assertNotNull(saved.getPid());
    assertNotNull(saved.getVersion());
    assertNotNull(saved.getCreationDate());
    assertNotNull(saved.getLastUpdate());
    assertEquals(edp, saved);
  }

  @Test
  void shouldUpdateExternalDataProvider() {
    // given
    long edpPid = 1L;
    Set<ExchangeProduction> exchanges = new HashSet<>(exchangeProductionRepository.findAll());
    ExternalDataProvider edp = externalDataProviderRepository.findById(edpPid).orElseThrow();
    int version = edp.getVersion();
    Date creationDate = edp.getCreationDate();
    Date updatedDate = edp.getLastUpdate();

    edp.setName("updated-name");
    edp.setBaseUrl("https://updated.url");
    edp.setEnablementStatus(ExternalDataProvider.EnablementStatus.INACTIVE);
    edp.setDescription("updated-description");
    edp.setDataProviderImplClass("updated-impl-class");
    edp.setFilterRequestRate(100);
    edp.setConfiguration("updated-configuration");
    edp.setBidRequestLocation(BidRequestLocation.User);
    edp.setBidRequestAttributeName("updated-attribute");
    edp.setBidderAliasRequired(false);
    edp.setExchanges(exchanges);

    // when
    ExternalDataProvider updated = externalDataProviderRepository.saveAndFlush(edp);

    // then
    assertEquals(edpPid, updated.getPid());
    assertEquals(version + 1, updated.getVersion());
    assertEquals(creationDate, updated.getCreationDate());
    assertNotEquals(updatedDate, updated.getLastUpdate());
    assertEquals(2, updated.getExchanges().size());
    assertEquals(edp, updated);
  }
}
