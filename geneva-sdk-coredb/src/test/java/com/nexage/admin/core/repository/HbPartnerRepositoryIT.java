package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.util.UUIDGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/hb-partner-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class HbPartnerRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired public HbPartnerRepository hbPartnerRepository;
  @Autowired private PositionRepository positionRepository;
  @Autowired private SiteRepository siteRepository;

  /* hbpartner entity create */
  @Test
  void testHbPartnerCreate() {

    HbPartner hbPartner = createHbPartner("TestPartner1");
    hbPartnerRepository.save(hbPartner);
    Long pid = hbPartner.getPid();
    String id = hbPartner.getId();
    boolean multiImpressionBid = hbPartner.isMultiImpressionBid();
    Integer maxAdsPerPod = hbPartner.getMaxAdsPerPod();

    List<HbPartner> hbPartnerList = hbPartnerRepository.findAll();
    assertEquals(4, hbPartnerList.size());
    Optional<HbPartner> hbPartnerResponse = hbPartnerRepository.findById(pid);
    if (hbPartnerResponse.isPresent()) {
      assertEquals(hbPartnerResponse.get().getPid(), pid);
      assertEquals(hbPartnerResponse.get().getId(), id);
      assertEquals(hbPartnerResponse.get().isMultiImpressionBid(), multiImpressionBid);
      assertEquals(hbPartnerResponse.get().getMaxAdsPerPod(), maxAdsPerPod);
      assertEquals("TestPartner1", hbPartnerResponse.get().getName());
      assertEquals(Status.ACTIVE, hbPartnerResponse.get().getStatus());
    }
  }

  /* hbpartner entity update */
  @Test
  void testHbPartnerUpdate() {
    HbPartner hbPartner = createHbPartner("TestPartner2");
    hbPartnerRepository.save(hbPartner);
    Long pid = hbPartner.getPid();
    Optional<HbPartner> hbPartnerResponse = hbPartnerRepository.findById(pid);
    if (hbPartnerResponse.isPresent()) {
      hbPartner = hbPartnerResponse.get();
      assertEquals(Status.ACTIVE, hbPartner.getStatus());
      assertTrue(hbPartner.isMultiImpressionBid());
      assertEquals(8, hbPartner.getMaxAdsPerPod());
      hbPartner.setStatus(Status.INACTIVE);
      hbPartner.setMaxAdsPerPod(null);
      hbPartner.setMultiImpressionBid(false);
      hbPartnerRepository.save(hbPartner);
      hbPartnerResponse = hbPartnerRepository.findById(pid);
      if (hbPartnerResponse.isPresent()) {
        hbPartner = hbPartnerResponse.get();
        assertEquals(Status.INACTIVE, hbPartner.getStatus());
        assertFalse(hbPartner.isMultiImpressionBid());
        assertNull(hbPartner.getMaxAdsPerPod());
      }
    }
  }

  /* test custom query to find active hb partners*/
  @Test
  void testHbPartnerFindAllActiveQuery() {
    HbPartner hbPartner1 = createHbPartner("TestPartner3");
    HbPartner hbPartner2 = createHbPartner("TestPartner4");
    hbPartnerRepository.save(hbPartner1);
    hbPartnerRepository.save(hbPartner2);
    List<HbPartner> hbPartnerList = hbPartnerRepository.findActiveHbPartners();
    assertEquals(5, hbPartnerList.size());
  }

  /* test soft delete */
  @Test
  void testHbPartnerSoftDelete() {
    HbPartner hbPartner = createHbPartner("TestPartner5");
    hbPartnerRepository.save(hbPartner);
    Long pid = hbPartner.getPid();
    Optional<HbPartner> hbPartnerResponse = hbPartnerRepository.findById(pid);
    if (hbPartnerResponse.isPresent()) {
      hbPartner = hbPartnerResponse.get();
      assertEquals(Status.ACTIVE, hbPartner.getStatus());
      hbPartnerRepository.deleteById(pid);
      List<HbPartner> hbPartnerList = hbPartnerRepository.findAll();
      assertEquals(4, hbPartnerList.size());
      hbPartner = hbPartnerList.get(3);
      assertEquals(Status.DELETED, hbPartner.getStatus());
    }
  }

  @Test
  void testFindDefaultPositionPerPartner() {
    List<HbPartnersAssociationView> hbPartnerList =
        positionRepository.findDefaultPositionsPerPartners(37L);
    assertEquals(2, hbPartnerList.size());
    assertEquals(Optional.of(45L).get(), hbPartnerList.get(0).getPid());
    assertEquals(Optional.of(12L).get(), hbPartnerList.get(0).getHbPartnerPid());
    assertEquals(Optional.of(1).get(), hbPartnerList.get(0).getType());
    assertEquals(Optional.of(45L).get(), hbPartnerList.get(1).getPid());
    assertEquals(Optional.of(6L).get(), hbPartnerList.get(1).getHbPartnerPid());
    assertEquals(Optional.of(2).get(), hbPartnerList.get(1).getType());
  }

  @Test
  void testFindDefaultSitePerPartner() {
    List<HbPartnersAssociationView> hbPartnerList = siteRepository.findDefaultSitesPerPartners(1L);
    assertEquals(1, hbPartnerList.size());
    assertEquals(Optional.of(37L).get(), hbPartnerList.get(0).getPid());
    assertEquals(Optional.of(11L).get(), hbPartnerList.get(0).getHbPartnerPid());
  }

  @Test
  void shouldFindPidsByCompanyPidTest() {
    // given
    List<Long> companypids = new ArrayList<>(List.of(11L));

    // when
    List<Long> returnedCompanyPids = hbPartnerRepository.findPidsByCompanyPid(1L);

    // then
    assertEquals(1, returnedCompanyPids.size(), "Invalid number of company pids");
    assertTrue(returnedCompanyPids.containsAll(companypids));
  }

  @Test
  void shouldReturnExpectedPidsWhenFindPidsWhichSupportFormattedDefaults() {
    // given
    List<Long> expectedHbPartnerPids = new ArrayList<>(List.of(6L));

    // when
    List<Long> returnedHbPartnerPids = hbPartnerRepository.findPidsWhichSupportFormattedDefaults();

    // then
    assertEquals(
        expectedHbPartnerPids.size(),
        returnedHbPartnerPids.size(),
        "Invalid number of hb partner pids");
    assertTrue(returnedHbPartnerPids.containsAll(expectedHbPartnerPids));
  }

  @Test
  void shouldReturnTrueOrFalseDependingOnWhetherHbPartnerSupportsMultiBidding() {
    // given
    Long multiBiddingSupportedHbPartnerPid = 11L;
    Long multiBiddingUnSupportedHbPartnerPid = 6L;

    // then
    assertTrue(
        hbPartnerRepository.isHbPartnerEnabledForMultiBidding(multiBiddingSupportedHbPartnerPid));
    assertFalse(
        hbPartnerRepository.isHbPartnerEnabledForMultiBidding(multiBiddingUnSupportedHbPartnerPid));
  }

  @Test
  void shouldReturnTrueOrFalseDependingOnWhetherHbPartnerSupportsFillMaxDuration() {
    // given
    Long fillMaxDurationSupportedHbPartnerPid = 11L;
    Long fillMaxDurationUnSupportedHbPartnerPid = 6L;

    // then
    assertTrue(
        hbPartnerRepository.isHbPartnerEnabledForFillMaxDuration(
            fillMaxDurationSupportedHbPartnerPid));
    assertFalse(
        hbPartnerRepository.isHbPartnerEnabledForFillMaxDuration(
            fillMaxDurationUnSupportedHbPartnerPid));
  }

  private HbPartner createHbPartner(String name) {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setStatus(Status.ACTIVE);
    hbPartner.setId(new UUIDGenerator().generateUniqueId());
    hbPartner.setName(name);
    hbPartner.setMultiImpressionBid(true);
    hbPartner.setMaxAdsPerPod(8);
    return hbPartner;
  }
}
