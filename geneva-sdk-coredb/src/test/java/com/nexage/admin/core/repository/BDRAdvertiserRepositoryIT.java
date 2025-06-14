package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.type.BDRStatus;
import com.nexage.admin.core.model.Company;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-advertiser-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BDRAdvertiserRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BDRAdvertiserRepository bdrAdvertiserRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private EntityManager entityManager;
  private static Company dbCompany;
  private static final Long COMPANY_PID = 1L;
  private static final Long BDR_ADVERTISER_PID = 1L;
  private static final String DOMAIN_NAME = "yahooinc.com";
  private static final String NEW_NAME = "test";
  private static final String EXISTING_NAME = "test-1";
  private static final BDRStatus STATUS = BDRStatus.ACTIVE;
  private static final String IAB_CATEGORY = "iab-cat";

  @BeforeEach
  void setUp() {
    if (dbCompany != null) return;
    dbCompany =
        companyRepository
            .findById(COMPANY_PID)
            .orElseThrow(() -> new EntityNotFoundException("Company Not Found in DB"));
  }

  @Test
  void shouldCreateBDRAdvertiser() {
    // given
    BDRAdvertiser advertiser = new BDRAdvertiser();
    advertiser.setCompany(dbCompany);
    advertiser.setStatus(STATUS);
    advertiser.setDomainName(DOMAIN_NAME);
    advertiser.setName(NEW_NAME);
    advertiser.setIabCategory(IAB_CATEGORY);

    // when
    advertiser = bdrAdvertiserRepository.save(advertiser);
    entityManager.refresh(advertiser);

    // then
    assertNotNull(advertiser.getPid());
    assertEquals(dbCompany, advertiser.getCompany());
    assertEquals(COMPANY_PID, advertiser.getCompanyPid());
    assertEquals(DOMAIN_NAME, advertiser.getDomainName());
    assertEquals(NEW_NAME, advertiser.getName());
    assertEquals(STATUS, advertiser.getStatus());
    assertEquals(IAB_CATEGORY, advertiser.getIabCategory());
  }

  @Test
  void shouldGetAdvertiserByPid() {
    // when
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository
            .findById(BDR_ADVERTISER_PID)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser Not Found in DB"));

    // then
    assertEquals(BDR_ADVERTISER_PID, advertiser.getPid());
    assertEquals(dbCompany, advertiser.getCompany());
    assertEquals(COMPANY_PID, advertiser.getCompanyPid());
    assertEquals(DOMAIN_NAME, advertiser.getDomainName());
    assertEquals(EXISTING_NAME, advertiser.getName());
    assertEquals(STATUS, advertiser.getStatus());
    assertEquals(IAB_CATEGORY, advertiser.getIabCategory());
  }

  @Test
  void shouldGetAdvertiserByNameAndCompanyPid() {
    // when
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository.findByNameAndCompanyPid(EXISTING_NAME, COMPANY_PID);

    // then
    assertNotNull(advertiser.getPid());
    assertEquals(dbCompany, advertiser.getCompany());
    assertEquals(COMPANY_PID, advertiser.getCompanyPid());
    assertEquals(DOMAIN_NAME, advertiser.getDomainName());
    assertEquals(EXISTING_NAME, advertiser.getName());
    assertEquals(STATUS, advertiser.getStatus());
    assertEquals(IAB_CATEGORY, advertiser.getIabCategory());
  }

  @Test
  void shouldGetAllAdvertiserForCompany() {
    // when
    List<BDRAdvertiser> companyAdvertisers = bdrAdvertiserRepository.findByCompanyPid(COMPANY_PID);

    // then
    assertEquals(2, companyAdvertisers.size());
    companyAdvertisers.forEach(advertiser -> assertEquals(COMPANY_PID, advertiser.getCompanyPid()));
  }

  @Test
  void shouldDeleteAdvertiser() {
    // given
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository
            .findById(BDR_ADVERTISER_PID)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser Not Found in DB"));

    // when
    bdrAdvertiserRepository.delete(advertiser);

    // then
    assertTrue(bdrAdvertiserRepository.findById(BDR_ADVERTISER_PID).isEmpty());
  }

  @Test
  void shouldFindAll() {
    // when
    List<BDRAdvertiser> bdrAdvertisers = bdrAdvertiserRepository.findAll();

    // then
    assertEquals(
        Set.of(1L, 2L, 3L),
        bdrAdvertisers.stream().map(BDRAdvertiser::getPid).collect(Collectors.toSet()));
  }
}
