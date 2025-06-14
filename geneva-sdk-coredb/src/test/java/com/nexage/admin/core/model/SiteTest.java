package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SiteTest {

  private final Site site = new Site();

  @Test
  void shouldAddPosition() {
    // given
    Set<Position> positions = new HashSet<>();
    Position position = new Position();
    position.setPid(1L);
    positions.add(position);
    site.setPositions(positions);
    Position positionToAdd = new Position();
    positionToAdd.setPid(2L);
    // when
    site.addPosition(positionToAdd);
    // then
    assertEquals(2, site.getPositions().size());
    assertTrue(site.getPositions().containsAll(Set.of(position, positionToAdd)));
  }

  @Test
  void
      shouldReturnImpressionGroupBasedOnGroupsEnabledAndImpressionGroupsWhenImpressionGroupIsNull() {
    // given
    Set<String> groups = Set.of("group1");
    site.setImpressionGroup(null);
    site.setGroupsEnabled(true);
    site.setImpressionGroups(groups);
    // when
    Site.ImpressionGroup impressionGroup = site.getImpressionGroup();
    // then
    assertEquals(groups, impressionGroup.getGroups());
    assertTrue(impressionGroup.isEnabled());
  }

  @Test
  void shouldReturnImpressionGroupWhenImpressionGroupIsNotNull() {
    // given
    Set<String> groups = Set.of("group1");
    site.setImpressionGroup(new Site.ImpressionGroup(true, groups));
    // when
    Site.ImpressionGroup impressionGroup = site.getImpressionGroup();
    // then
    assertEquals(groups, impressionGroup.getGroups());
    assertTrue(impressionGroup.isEnabled());
  }

  @Test
  void shouldProperlySetDescriptionWhenNotEmptyAfterTrim() {
    // given
    String validDescription = "valid description";
    // when
    site.setDescription(validDescription);
    // then
    assertEquals(validDescription, site.getDescription());
  }

  @Test
  void shouldSetDescriptionToNullWhenEmptyAfterTrim() {
    // given
    String emptyDescriptionAfterTrim = "   ";
    // when
    site.setDescription(emptyDescriptionAfterTrim);
    // then
    assertNull(site.getDescription());
  }

  @Test
  void shouldNotFailWhenSetDescriptionToNull() {
    // when
    site.setDescription(null);
    // then
    assertNull(site.getDescription());
  }

  @Test
  void shouldSetStatusAndReturnStatusValBasedOnStatus() {
    // given
    Status status = Status.ACTIVE;
    // when
    site.setStatus(status);
    // then
    assertEquals(status, site.getStatus());
    assertEquals(status.asInt(), site.getStatusVal());
  }

  @Test
  void shouldSetStatusValAndStatus() {
    // given
    Integer statusVal = 1;
    // when
    site.setStatusVal(statusVal);
    // then
    assertEquals(statusVal, site.getStatusVal());
    assertEquals(Status.fromInt(statusVal), site.getStatus());
  }

  @Test
  void shouldReturnEmptySetWhenPassthruParametersNull() {
    // given
    site.setPassthruParameters(null);
    // when
    Set<String> passthruParameters = site.getPassthruParameters();
    // then
    assertTrue(passthruParameters.isEmpty());
  }

  @Test
  void shouldReturnPassthruParametersWhenNotNull() {
    // given
    site.setPassthruParameters(Set.of("param1"));
    // when
    Set<String> passthruParameters = site.getPassthruParameters();
    // then
    assertEquals(1, passthruParameters.size());
    assertTrue(passthruParameters.contains("param1"));
  }

  @Test
  void shouldReturnDefaultPositionsAndSetDefaultPositionNamesForDefaultPosition() {
    // given
    String defaultPositionString = "defaultPosition";
    Set<String> defaultPositions = Set.of(defaultPositionString);
    Position defaultPosition = new Position();
    defaultPosition.setIsDefault(true);
    site.setDefaultPositions(defaultPositions);
    site.setPositions(Set.of(defaultPosition));
    // when
    Set<String> result = site.getDefaultPositions();
    // then
    assertEquals(defaultPositions, result);
    assertEquals(defaultPositionString, defaultPosition.getDefaultPositionNames());
  }

  @Test
  void shouldReturnDefaultPositionsAndNotSetDefaultPositionNamesForNonDefaultPosition() {
    // given
    String defaultPositionString = "defaultPosition";
    Set<String> defaultPositions = Set.of(defaultPositionString);
    Position defaultPosition = new Position();
    defaultPosition.setIsDefault(false);
    site.setDefaultPositions(defaultPositions);
    site.setPositions(Set.of(defaultPosition));
    // when
    Set<String> result = site.getDefaultPositions();
    // then
    assertEquals(defaultPositions, result);
    assertNull(defaultPosition.getDefaultPositionNames());
  }

  @Test
  void shouldReturnDefaultPositionsWhenEmpty() {
    // when & then
    assertTrue(site.getDefaultPositions().isEmpty());
  }

  @Test
  void shouldAddToDealTermsWhenEmpty() {
    // given
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setPid(1L);
    // when
    site.addToDealTerms(siteDealTerm);
    // then
    assertEquals(1, site.getDealTerms().size());
    assertTrue(site.getDealTerms().contains(siteDealTerm));
  }

  @Test
  void shouldAddToDealTermsWhenNotEmpty() {
    // given
    Set<SiteDealTerm> currentSiteDealTerms = new HashSet<>();
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setPid(1L);
    currentSiteDealTerms.add(siteDealTerm);
    SiteDealTerm siteDealTermToAdd = new SiteDealTerm();
    siteDealTermToAdd.setPid(2L);
    site.setDealTerms(currentSiteDealTerms);
    // when
    site.addToDealTerms(siteDealTermToAdd);
    // then
    assertEquals(2, site.getDealTerms().size());
    assertTrue(site.getDealTerms().containsAll(Set.of(siteDealTerm, siteDealTermToAdd)));
  }

  @Test
  void shouldReturnCurrentDealTermWhenNotNull() {
    // given
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    site.setCurrentDealTerm(siteDealTerm);
    // when & then
    assertEquals(siteDealTerm, site.getCurrentDealTerm());
  }

  @Test
  void shouldSetAndReturnCurrentDealTermWhenNull() {
    // given
    Long tagPid = 1L;
    Tag tag = new Tag();
    tag.setPid(tagPid);
    SiteDealTerm tagSiteDealTerm = new SiteDealTerm();
    tagSiteDealTerm.setTagPid(tagPid);
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setPid(2L);
    site.setTags(Set.of(tag));
    site.setDealTerms(Set.of(siteDealTerm, tagSiteDealTerm));
    // when
    SiteDealTerm result = site.getCurrentDealTerm();
    // then
    assertEquals(siteDealTerm, result);
    assertEquals(tagSiteDealTerm, tag.getCurrentDealTerm());
  }

  @Test
  void shouldClearAndAddAllElementsWhenSetHbPartnerSite() {
    // given
    Set<HbPartnerSite> existingHbPartnerSites = new HashSet<>();
    HbPartnerSite hbPartnerSite1 = new HbPartnerSite();
    hbPartnerSite1.setPid(1L);
    existingHbPartnerSites.add(hbPartnerSite1);
    site.setHbPartnerSite(existingHbPartnerSites);

    Set<HbPartnerSite> hbPartnerSitesToSet = new HashSet<>();
    HbPartnerSite hbPartnerSite2 = new HbPartnerSite();
    hbPartnerSite2.setPid(2L);
    hbPartnerSitesToSet.add(hbPartnerSite2);
    // when
    site.setHbPartnerSite(hbPartnerSitesToSet);
    // then
    assertEquals(1, site.getHbPartnerSite().size());
    assertTrue(site.getHbPartnerSite().containsAll(hbPartnerSitesToSet));
  }

  @Test
  void shouldSetLastUpdateAndRevenueLaunchDatePreUpdate() {
    // given
    site.setLive(true);
    // when
    site.onUpdate();
    // then
    assertNotNull(site.getLastUpdate());
    assertEquals(site.getLastUpdate(), site.getRevenueLaunchDate());
  }

  @Test
  void shouldNotSetRevenueLaunchDatePreUpdateWhenNotLive() {
    // given
    site.setLive(false);
    // when
    site.onUpdate();
    // then
    assertNull(site.getRevenueLaunchDate());
  }

  @Test
  void shouldSetDefaultValuesOnCreate() {
    // given
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    site.setCurrentDealTerm(siteDealTerm);
    Set<String> groups = Set.of("group1");
    Site.ImpressionGroup impressionGroup = new Site.ImpressionGroup(true, groups);
    site.setImpressionGroup(impressionGroup);
    site.setType(Type.MOBILE_WEB);
    site.setLive(true);
    site.setInputDateFormat("");
    // when
    site.onCreate();
    // then
    assertEquals(500, site.getBuyerTimeout());
    assertEquals(0, site.getDaysFree());
    assertFalse(site.getDcn().isBlank());
    assertFalse(site.getId().isBlank());
    assertEquals(10, site.getReportBatchSize());
    assertEquals(180000, site.getReportFrequency());
    assertEquals(1800000, site.getRulesUpdateFrequency());
    assertEquals(Status.INACTIVE, site.getStatus());
    assertEquals(5000, site.getTotalTimeout());
    assertEquals(0, site.getTrafficThrottle());
    assertEquals(Platform.OTHER, site.getPlatform());
    assertNotNull(site.getLastUpdate());
    assertEquals(site.getLastUpdate(), site.getCreationDate());
    assertEquals(site.getLastUpdate(), site.getRevenueLaunchDate());
    assertEquals(site.getLastUpdate(), siteDealTerm.getEffectiveDate());
    assertTrue(site.isGroupsEnabled());
    assertEquals(groups, site.getImpressionGroups());
    assertNull(site.getInputDateFormat());
  }

  @Test
  void shouldSetDefaultValuesWhenEqualToZeroOnCreate() {
    // given
    site.setBuyerTimeout(0);
    site.setReportBatchSize(0);
    site.setReportFrequency(0);
    site.setRulesUpdateFrequency(0);
    site.setTotalTimeout(0);
    // when
    site.onCreate();
    // then
    assertEquals(500, site.getBuyerTimeout());
    assertEquals(10, site.getReportBatchSize());
    assertEquals(180000, site.getReportFrequency());
    assertEquals(1800000, site.getRulesUpdateFrequency());
    assertEquals(5000, site.getTotalTimeout());
  }

  @Test
  void shouldNotSetDefaultDefaultValuesWhenAlreadySetOnCreate() {
    // given
    site.setBuyerTimeout(1);
    site.setDaysFree(2);
    site.setDcn("dcn");
    site.setReportBatchSize(3);
    site.setReportFrequency(4);
    site.setRulesUpdateFrequency(5);
    site.setStatus(Status.ACTIVE);
    site.setTotalTimeout(6);
    site.setTrafficThrottle(7);
    site.setPlatform(Platform.ANDROID);
    site.setInputDateFormat("inputDateFormat");
    Date date = new Date();
    site.setRevenueLaunchDate(date);
    // when
    site.onCreate();
    // then
    assertEquals(1, site.getBuyerTimeout());
    assertEquals(2, site.getDaysFree());
    assertEquals("dcn", site.getDcn());
    assertEquals(3, site.getReportBatchSize());
    assertEquals(4, site.getReportFrequency());
    assertEquals(5, site.getRulesUpdateFrequency());
    assertEquals(Status.ACTIVE, site.getStatus());
    assertEquals(6, site.getTotalTimeout());
    assertEquals(7, site.getTrafficThrottle());
    assertEquals(Platform.ANDROID, site.getPlatform());
    assertEquals("inputDateFormat", site.getInputDateFormat());
    assertEquals(date, site.getRevenueLaunchDate());
  }
}
