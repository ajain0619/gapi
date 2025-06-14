package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.projections.SiteWithInactiveTagProjection;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.tag.TagCleanupResultsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.validator.RTBProfileValidator;
import com.nexage.app.util.validator.TagValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
class SellerTagServiceTest {

  private static final Long SITE_PID = 1L;
  private static final Long POSITION_PID = 2L;
  private static final String POSITION_NAME = "positionName";
  private static final Long TIER_PID = 3L;
  private static final Long TAG_PID = 4L;

  @Mock UserContext userContext;
  @Mock private SiteRepository siteRepository;
  @Mock private TagRepository tagRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private PhoneCastConfigService phoneCastConfigService;
  @Mock private EntityManager entityManager;

  @Mock
  @Qualifier("coreServicesJdbcTemplate")
  protected JdbcTemplate jdbcTemplate;

  @Mock
  @Qualifier("coreNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate coreNamedTemplate;

  @Mock
  @Qualifier("dwNamedJdbcTemplate")
  protected NamedParameterJdbcTemplate dwNamedTemplate;

  @Mock private SellerSiteService sellerSiteService;
  @Mock private TagValidator tagValidator;
  @Mock private RTBProfileValidator rtbProfileValidator;
  @Mock private RTBProfileUtil rtbProfileUtil;

  @InjectMocks SellerTagServiceImpl sellerTagService;

  private final Tag tag = new Tag();
  private final RTBProfile rtbProfile = new RTBProfile();
  private SiteWithInactiveTagProjection siteWithInactiveTagProjection;

  @BeforeEach
  void setUp() {
    tag.setPid(10L);
    tag.setName("Test-Tag");
    tag.setPrimaryId("TestId");

    rtbProfile.setExchangeSiteTagId("TestId");
  }

  @Test
  void shouldUpdateTag() {
    Site site = getSite();

    Site expectedSite = getSite();
    tag.setName("Updated-Test-Tag");

    Set<Tag> expectedTags = new HashSet<>();
    expectedTags.add(tag);

    expectedSite.setTags(expectedTags);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.updateTag(tag);

    assertEquals("Updated-Test-Tag", returnedSite.getTags().iterator().next().getName());
  }

  @Test
  void shouldCreateExchangeTag() {
    Site site = getSite();

    site.setTags(null);

    Site expectedSite = getSite();
    Set<Tag> expectedTags = new HashSet<>();
    expectedTags.add(tag);

    expectedSite.setTags(expectedTags);

    when(siteRepository.saveAndFlush(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.createExchangeTag(site.getPid(), tag, null, false);

    assertEquals(
        expectedSite.getTags().iterator().next(), returnedSite.getTags().iterator().next());
  }

  @Test
  void shouldThrowWhenCreatingExchangeTagAndSitePidMismatchesTagSitePid() {
    Site site = getSite();

    tag.setSitePid(120L);

    rtbProfile.setSitePid(120L);
    Long pid = site.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerTagService.createExchangeTag(pid, tag, rtbProfile, false));
    assertEquals(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH, exception.getErrorCode());
  }

  @Test
  void shouldUpdateExchangeTag() {
    rtbProfile.setPid(122L);

    Site site = getSite();

    Set<RTBProfile> rtbProfiles = new HashSet<>();
    rtbProfiles.add(rtbProfile);

    site.setRtbProfiles(rtbProfiles);

    Site expectedSite = getSite();
    tag.setName("New update name");
    Set<Tag> updatedTags = new HashSet<>();
    updatedTags.add(tag);

    expectedSite.setTags(updatedTags);
    expectedSite.setRtbProfiles(rtbProfiles);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.updateExchangeTag(tag, rtbProfile);

    assertEquals(
        expectedSite.getTags().iterator().next().getName(),
        returnedSite.getTags().iterator().next().getName());
  }

  @Test
  void shouldCreateTag() {
    Site site = getSite();

    Site expectedSite = getSite();

    Tag newTag = new Tag();
    newTag.setPid(2L);
    newTag.setName("New Tag");

    Set<Tag> tags = new HashSet<>();
    tags.add(newTag);
    tags.add(tag);
    expectedSite.setTags(tags);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.saveAndFlush(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.createTag(site.getPid(), tag, false);

    assertEquals(expectedSite.getTags(), returnedSite.getTags());
  }

  @Test
  void shouldThrowWhenCreatingTagAndProvidedSitePidMismatchesTagSitePid() {
    Site site = getSite();

    tag.setSitePid(120L);
    Long pid = site.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerTagService.createTag(pid, tag, false));
    assertEquals(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH, exception.getErrorCode());
  }

  @Test
  void shouldDeleteTag() {
    Site site = getSite();

    Site expectedSite = getSite();
    expectedSite.setTags(new HashSet<>());

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.deleteTag(site.getPid(), tag.getPid());

    assertEquals(0, returnedSite.getTags().size());
  }

  @Test
  void shouldThrowWhenDeletingNonexistentTag() {
    Site site = getSite();

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    Long pid = site.getPid();
    var exception =
        assertThrows(GenevaValidationException.class, () -> sellerTagService.deleteTag(pid, 100L));
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldDeleteExchangeTag() {
    Site site = getSite();

    Set<RTBProfile> rtbProfiles = new HashSet<>();
    rtbProfiles.add(rtbProfile);

    site.setRtbProfiles(rtbProfiles);

    Site expectedSite = getSite();

    expectedSite.setTags(new HashSet<>());

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerTagService.deleteExchangeTag(site.getPid(), tag.getPid());

    assertEquals(0, returnedSite.getTags().size());
  }

  @Test
  void shouldFindAnyDealTermMatchingTagPid() {
    // given
    Long tagPid = 123L;
    SiteDealTerm expectedTerm =
        new SiteDealTerm() {
          {
            setTagPid(tagPid);
          }
        };
    Set<SiteDealTerm> terms = getSiteDealTerms();
    terms.add(expectedTerm);

    // when
    SiteDealTerm result = SellerTagServiceImpl.findAnyDealTermMatchingTagPid(terms, tagPid);

    // then
    assertSame(expectedTerm, result);
  }

  @Test
  void shouldDefaultToNullWhenFindingAnyDealTermMatchingTagPidWithNullTagPid() {
    // given
    Long tagPid = null;
    Set<SiteDealTerm> terms = getSiteDealTerms();

    // when
    SiteDealTerm result = SellerTagServiceImpl.findAnyDealTermMatchingTagPid(terms, tagPid);

    // then
    assertNull(result);
  }

  @Test
  void shouldDefaultToNullWhenFindingAnyDealTermMatchingTagPidWithMismatchingTagPid() {
    // given
    Long tagPid = 123L;
    Set<SiteDealTerm> terms = getSiteDealTerms();

    // when
    SiteDealTerm result = SellerTagServiceImpl.findAnyDealTermMatchingTagPid(terms, tagPid);

    // then
    assertNull(result);
  }

  @Test
  void shouldRetrieveTagsForPublisherAndAdSource() {
    // given
    long publisherPid = 1L;
    long adSourcePid = 2L;
    List<Tag> tags = List.of(new Tag());
    given(tagRepository.findForAdSource(publisherPid, adSourcePid)).willReturn(tags);

    // when
    List<Tag> result = sellerTagService.getPubAdsourceTags(publisherPid, adSourcePid);

    // then
    assertSame(tags, result);
  }

  @Test
  void shouldCleanupTagDeploymentsBySitePid() {
    // given
    Site site = getSiteWithInactiveTag();
    mockSiteWithInactiveTagProjection();
    given(siteRepository.findSiteWithInactiveTagProjectionsByPid(SITE_PID))
        .willReturn(List.of(siteWithInactiveTagProjection));
    given(sellerSiteService.getSite(SITE_PID)).willReturn(site);

    // when
    TagCleanupResultsDTO result = sellerTagService.cleanupTagDeployments(SITE_PID);

    // then
    assertEquals(1, result.getTagsRemoved());
    assertEquals(1, result.getTiersRemoved());
  }

  @Test
  void shouldCleanupAllTagDeployments() {
    // given
    Site site = getSiteWithInactiveTag();
    mockSiteWithInactiveTagProjection();
    given(siteRepository.findAllSiteWithInactiveTagProjections())
        .willReturn(List.of(siteWithInactiveTagProjection));
    given(sellerSiteService.getSite(SITE_PID)).willReturn(site);

    // when
    TagCleanupResultsDTO result = sellerTagService.cleanupTagDeployments(-1L);

    // then
    assertEquals(1, result.getTagsRemoved());
    assertEquals(1, result.getTiersRemoved());
  }

  private Site getSiteWithInactiveTag() {
    Tag tag = new Tag();
    tag.setPid(TAG_PID);
    tag.setStatus(Status.INACTIVE);

    Tier tier = new Tier();
    tier.setPid(TIER_PID);
    tier.addTag(tag);

    Position position = new Position();
    position.setPid(POSITION_PID);
    position.setName(POSITION_NAME);
    position.setTiers(List.of(tier));

    Site site = new Site();
    site.setPid(SITE_PID);
    site.addPosition(position);
    return site;
  }

  private void mockSiteWithInactiveTagProjection() {
    siteWithInactiveTagProjection = mock(SiteWithInactiveTagProjection.class);
    given(siteWithInactiveTagProjection.getSitePid()).willReturn(SITE_PID);
    given(siteWithInactiveTagProjection.getPositionPid()).willReturn(POSITION_PID);
    given(siteWithInactiveTagProjection.getPositionName()).willReturn(POSITION_NAME);
    given(siteWithInactiveTagProjection.getTierPid()).willReturn(TIER_PID);
    given(siteWithInactiveTagProjection.getTagPid()).willReturn(TAG_PID);
  }

  private Set<SiteDealTerm> getSiteDealTerms() {
    Set<SiteDealTerm> terms = new HashSet<>();
    terms.add(null);
    terms.add(new SiteDealTerm());
    terms.add(
        new SiteDealTerm() {
          {
            setTagPid(456L);
          }
        });
    return terms;
  }

  private Site getSite() {
    tag.setSitePid(1L);
    tag.setPid(10L);
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);

    Site site = new Site();
    site.setId("test-id");
    site.setPid(1L);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    site.setCompanyPid(10L);
    site.setTags(tags);
    return site;
  }
}
