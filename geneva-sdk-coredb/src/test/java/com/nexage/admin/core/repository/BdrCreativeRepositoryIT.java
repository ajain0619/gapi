package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.type.BDRStatus;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-creative-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrCreativeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  public static final String MODIFIER_STRING = "#";
  public static final Integer MODIFIER_INTEGER = 10;
  public static final long CREATIVE_ID_ONE = 1L;
  public static final long ADERTISER_ID_ONE = 1L;
  public static final long ADERTISER_ID_TWO = 2L;
  public static final long COMPANY_PID = 1L;
  public static final String CREATIVE_NAME_FOUND = "BDRCreative0";
  public static final String CREATIVE_NAME_NOT_FOUND = "BDRCreative1";

  @Autowired protected BDRAdvertiserRepository bdrAdvertiserRepository;
  @Autowired protected EntityManager entityManager;
  @Autowired protected BdrCreativeRepository bdrCreativeRepository;

  @Test
  void shouldSaveBdrCreative() {
    // given
    BdrCreative bdrCreative =
        buildBDRCreative("BDRCreative0", BDRStatus.ACTIVE, "BDRAdvertiser0", 1);

    // when
    BdrCreative result = bdrCreativeRepository.save(bdrCreative);

    // then
    assertNotNull(result.getPid());

    assertEquals(bdrCreative.getName(), result.getName());
  }

  @Test
  void shouldFindAll() {
    // when
    List<BdrCreative> creatives = bdrCreativeRepository.findAll();

    // then
    assertEquals(4, creatives.size());
  }

  @Test
  void shouldUpdateBdrCreative() {
    // given
    BdrCreative creative = bdrCreativeRepository.findById(CREATIVE_ID_ONE).orElse(null);
    creative.setAdvertiser(bdrAdvertiserRepository.findById(ADERTISER_ID_TWO).orElse(null));
    creative.setName(creative.getName() + MODIFIER_STRING);
    creative.setStatus(BDRStatus.INACTIVE);
    creative.setBannerURL(creative.getBannerURL() + MODIFIER_STRING);
    creative.setCustomMarkup(creative.getCustomMarkup() + MODIFIER_STRING);
    creative.setIndicativeURL(creative.getIndicativeURL() + MODIFIER_STRING);
    creative.setLandingURL(creative.getLandingURL() + MODIFIER_STRING);
    creative.setTrackingURL(creative.getTrackingURL() + MODIFIER_STRING);
    creative.setHeight(creative.getHeight() - MODIFIER_INTEGER);
    creative.setWidth(creative.getWidth() - MODIFIER_INTEGER);
    creative.setVersion(creative.getVersion() + MODIFIER_INTEGER);

    // when
    bdrCreativeRepository.save(creative);
    Optional<BdrCreative> result = bdrCreativeRepository.findById(creative.getPid());

    // then
    assertEquals(creative, result.get());
  }

  @Test
  void shouldCheckWhetherBdrCreativeExists() {
    assertTrue(
        bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(
            CREATIVE_NAME_FOUND, COMPANY_PID));
    assertFalse(
        bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(
            CREATIVE_NAME_NOT_FOUND, COMPANY_PID));
  }

  @Test
  void shouldGetBdrCreative() {
    // when
    Optional<BdrCreative> dbCreative =
        bdrCreativeRepository.findByPidAndAdvertiser_Pid(CREATIVE_ID_ONE, ADERTISER_ID_ONE);

    // then
    assertTrue(dbCreative.isPresent());
    assertEquals(CREATIVE_NAME_FOUND, dbCreative.get().getName());
  }

  private BdrCreative buildBDRCreative(
      String name, BDRStatus status, String advertiserName, Integer version) {
    BdrCreative object = new BdrCreative();
    object.setAdvertiser(bdrAdvertiserRepository.findById(1L).orElse(null));
    object.setName(name);
    object.setStatus(status);
    object.setBannerURL("http://banner." + name);
    object.setCustomMarkup("<div>" + name + "</div>");
    object.setIndicativeURL("http://indicative." + name);
    object.setLandingURL("http://landing." + name);
    object.setTrackingURL("http://tracking." + name);
    object.setHeight(800);
    object.setWidth(600);
    object.setNexageBannerUrl("host");
    object.setVersion(version);

    return object;
  }
}
