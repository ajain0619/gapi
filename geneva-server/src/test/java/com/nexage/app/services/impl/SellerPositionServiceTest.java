package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CampaignService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SellerPositionServiceTest {

  @Mock private UserContext userContext;
  @Mock private SiteRepository siteRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private CampaignService campaignService;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private SellerSiteService sellerSiteService;
  @Mock private PositionValidator positionValidator;
  @Mock private RTBProfileUtil rtbProfileUtil;

  @InjectMocks private SellerPositionServiceImpl sellerPositionService;

  private Position position = new Position();

  @BeforeEach
  void setUp() {
    position.setPid(123L);
    position.setWidth(2);
    position.setHeight(3);
    position.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    position.setPlacementCategory(PlacementCategory.BANNER);
  }

  @Test
  void shouldGetPosition() {
    when(positionRepository.findById(anyLong())).thenReturn(Optional.of(position));

    Position responsePosition = sellerPositionService.getPosition(position.getPid());

    assertEquals(position, responsePosition);
  }

  @Test
  void shouldThrowExceptionWhenPositionNotFoundOnGet() {
    when(positionRepository.findById(anyLong())).thenReturn(Optional.empty());
    var pid = position.getPid();

    var exception =
        assertThrows(GenevaValidationException.class, () -> sellerPositionService.getPosition(pid));
    assertEquals(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS, exception.getErrorCode());
  }

  @Test
  void shouldCreatePosition() {
    Site site = new Site();
    site.setPid(10L);

    Site expectedSite = new Site();
    expectedSite.addPosition(position);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerPositionService.createPosition(site.getPid(), position);
    verify(positionRepository).save(position);
    assertEquals(position, returnedSite.getPositions().iterator().next());
  }

  @Test
  void shouldThrowExceptionWhenSitePidMismatches() {
    var position =
        new Position() {
          {
            ReflectionTestUtils.setField(this, "sitePid", 2L);
          }
        };
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerPositionService.createPosition(1L, position));
    assertEquals(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH, exception.getErrorCode());
  }

  @Test
  void shouldDeletePosition() {
    Site site = getSite();
    site.addPosition(position);

    Site expectedSite = getSite();

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(siteRepository.save(any(Site.class))).thenReturn(expectedSite);

    Site returnedSite = sellerPositionService.deletePosition(site.getPid(), position.getPid());

    assertEquals(expectedSite, returnedSite);
  }

  @Test
  void shouldArchivePosition() {
    // given
    Site site = getSite();
    site.addPosition(position);
    position.setStatus(Status.DELETED);
    Site expectedSite = getSite();
    expectedSite.addPosition(position);
    given(siteRepository.saveAndFlush(any(Site.class))).willReturn(expectedSite);

    // when
    Site returnedSite = sellerPositionService.archivePosition(site, position.getPid());

    // then
    assertEquals(Status.DELETED, returnedSite.getPositions().iterator().next().getStatus());
  }

  @Test
  void shouldArchivePositionWhenNoPositionsInSite() {
    Site site = getSite();
    Long pid = position.getPid();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerPositionService.archivePosition(site, pid));

    assertEquals(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE, exception.getErrorCode());
  }

  @Test
  void shouldAssignProfileToPosition() {
    // Given
    Position position = new Position();
    given(positionRepository.findById(anyLong())).willReturn(Optional.of(new Position()));
    RTBProfile rtbProfile = new RTBProfile();
    given(rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndPid(anyLong(), anyLong()))
        .willReturn(Optional.of(new RTBProfile()));
    position.setDefaultRtbProfile(rtbProfile);

    // When
    sellerPositionService.assignRTBProfileToPosition(1L, 2L, 3L);

    // Then
    verify(positionRepository).saveAndFlush(position);
  }

  private Site getSite() {
    return getSite(1L);
  }

  private Site getSite(long pid) {
    Site site = new Site();
    site.setId("test-id");
    site.setPid(pid);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    site.setCompanyPid(10l);
    return site;
  }
}
