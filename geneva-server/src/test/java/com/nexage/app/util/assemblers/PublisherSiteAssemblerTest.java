package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.impl.CompanyServiceImpl;
import com.nexage.app.services.impl.HbPartnerServiceImpl;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PublisherSiteAssemblerTest {

  private Position position;
  private HbPartnerAssignmentDTO hbPartnerAssignmentDto;
  private HbPartnerSite hbPartnerSite;
  private PublisherSiteDTO publisherSiteDto;
  private PublisherSiteDealTermDTO publisherSiteDealTermDto;
  private Site site;

  @Mock private CompanyServiceImpl companyService;
  @Mock private HbPartnerServiceImpl hbPartnerService;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private UserContext userContext;

  @InjectMocks private PublisherSiteAssembler publisherSiteAssembler;

  private static final long PUBLISHER_PID = 123L;
  private static final long HB_PARTNER_PID = 1L;

  @BeforeEach
  void setup() {
    hbPartnerAssignmentDto = new HbPartnerAssignmentDTO();
    hbPartnerSite = new HbPartnerSite();
    position = new Position();
    publisherSiteDto = new PublisherSiteDTO();
    publisherSiteDealTermDto = new PublisherSiteDealTermDTO();
    site = new Site();
  }

  @Test
  void shouldFillHbPartnersWithEmptySetWhenDtoNull() {
    // given
    publisherSiteDto.setHbPartnerAttributes(null);
    site.setHbPartnerSite(Set.of(hbPartnerSite));

    // when
    ReflectionTestUtils.invokeMethod(
        publisherSiteAssembler, "fillHbPartnerAttributes", PUBLISHER_PID, site, publisherSiteDto);

    // then
    assertEquals(Collections.emptySet(), site.getHbPartnerSite(), "Hb Partner should be empty");
  }

  @Test
  void shouldThrowExceptionWhenPidPresentButExternalIdAbsentAndNonDefaultAssociation() {
    // given
    hbPartnerAssignmentDto.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDto.setExternalId(null);
    hbPartnerAssignmentDto.setType(AssociationType.NON_DEFAULT);
    publisherSiteDto.setHbPartnerAttributes(Set.of(hbPartnerAssignmentDto));

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    publisherSiteAssembler,
                    "fillHbPartnerAttributes",
                    PUBLISHER_PID,
                    site,
                    publisherSiteDto));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenHbPartnerNotPresentInCompany() {
    // given
    hbPartnerAssignmentDto.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDto.setExternalId("external_id");
    publisherSiteDto.setHbPartnerAttributes(Set.of(hbPartnerAssignmentDto));

    given(hbPartnerService.findPidsByCompanyPid(anyLong())).willReturn(List.of(123L));

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    publisherSiteAssembler,
                    "fillHbPartnerAttributes",
                    PUBLISHER_PID,
                    site,
                    publisherSiteDto));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_ASSIGNMENT_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldFillDealTermWhenNonePreexisting() {
    // given
    publisherSiteDealTermDto.setNexageRevenueShare(new BigDecimal("0.25"));
    publisherSiteDealTermDto.setRtbFee(new BigDecimal("0.1"));

    given(userContext.isNexageUser()).willReturn(true);

    // when
    ReflectionTestUtils.invokeMethod(
        publisherSiteAssembler, "fillDealTerms", PUBLISHER_PID, site, publisherSiteDealTermDto);

    // then
    assertNotNull(site.getCurrentDealTerm());
    assertNotNull(site.getCurrentDealTerm().getEffectiveDate());
  }

  @Test
  void shouldFillDealTermsWhenPreviousExists() {
    // given
    var existingSiteDealTerm = new SiteDealTerm();

    existingSiteDealTerm.setNexageRevenueShare(new BigDecimal("0.23"));
    existingSiteDealTerm.setRtbFee(new BigDecimal("0.12"));
    existingSiteDealTerm.setEffectiveDate(Instant.now().minus(1).toDate());
    publisherSiteDealTermDto.setNexageRevenueShare(new BigDecimal("0.25"));
    publisherSiteDealTermDto.setRtbFee(new BigDecimal("0.1"));

    given(userContext.isNexageUser()).willReturn(true);

    // when
    ReflectionTestUtils.invokeMethod(
        publisherSiteAssembler, "fillDealTerms", PUBLISHER_PID, site, publisherSiteDealTermDto);

    // then
    assertNotNull(site.getCurrentDealTerm());
    assertNotNull(site.getCurrentDealTerm().getEffectiveDate());
    assertTrue(
        existingSiteDealTerm.getEffectiveDate().getTime()
            < site.getCurrentDealTerm().getEffectiveDate().getTime());
  }

  @Test
  void shouldFillHbPartnerAttributesWithNoAssignment() {
    // when
    ReflectionTestUtils.invokeMethod(
        publisherSiteAssembler, "fillHbPartnerAttributes", PUBLISHER_PID, site, publisherSiteDto);

    // then
    assertNotNull(site.getHbPartnerSite());
    assertTrue(site.getHbPartnerSite().isEmpty());
  }

  @Test
  void shouldFillHbPartnerAttributesWithAssignment() {
    // given
    var hbPartner = new HbPartner();

    hbPartnerAssignmentDto.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDto.setExternalId("external_id");
    publisherSiteDto.setHbPartnerAttributes(Set.of(hbPartnerAssignmentDto));
    hbPartner.setPid(123L);

    given(hbPartnerService.findPidsByCompanyPid(anyLong())).willReturn(List.of(HB_PARTNER_PID));
    given(hbPartnerRepository.findById(anyLong())).willReturn(Optional.of(hbPartner));

    // when
    ReflectionTestUtils.invokeMethod(
        publisherSiteAssembler, "fillHbPartnerAttributes", PUBLISHER_PID, site, publisherSiteDto);

    // then
    assertNotNull(site.getHbPartnerSite());
    assertFalse(site.getHbPartnerSite().isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenSiteTypeDooh() {
    // given
    site.setType(Type.DOOH);
    position.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    position.setVideoSupport(VideoSupport.VIDEO);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    publisherSiteAssembler, "checkPositionVideoSupport", site, position));
    assertEquals(
        ServerErrorCodes.SERVER_SITE_INTEGRATION_IS_NOT_CHANGEABLE_CONTAINS_VIDEO_PLACEMENT,
        exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenSiteTypeDesktopWithIntegrationJS() {
    // given
    site.setType(Type.DESKTOP);
    position.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    position.setVideoSupport(VideoSupport.VIDEO);

    // when & then
    assertDoesNotThrow(
        () ->
            ReflectionTestUtils.invokeMethod(
                publisherSiteAssembler, "checkPositionVideoSupport", site, position));
  }

  @Test
  void shouldNotThrowExceptionWhenSiteTypeDesktopWithInterstitial() {
    // given
    site.setType(Type.DESKTOP);
    position.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    position.setVideoSupport(VideoSupport.VIDEO);

    // when & then
    assertDoesNotThrow(
        () ->
            ReflectionTestUtils.invokeMethod(
                publisherSiteAssembler, "checkPositionVideoSupport", site, position));
  }

  @Test
  void shouldNotThrowExceptionWhenSiteTypeDesktopWithBannerVideoSupport() {
    // given
    site.setType(Type.DESKTOP);
    position.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    position.setVideoSupport(VideoSupport.BANNER);

    // when & then
    assertDoesNotThrow(
        () ->
            ReflectionTestUtils.invokeMethod(
                publisherSiteAssembler, "checkPositionVideoSupport", site, position));
  }

  @Test
  void shouldNotThrowExceptionWhenSiteTypeDesktopWithAPI() {
    // given
    site.setType(Type.DESKTOP);
    position.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    position.setVideoSupport(VideoSupport.BANNER);

    // when & then
    assertDoesNotThrow(
        () ->
            ReflectionTestUtils.invokeMethod(
                publisherSiteAssembler, "checkPositionVideoSupport", site, position));
  }

  @Test
  void shouldNotThrowExceptionWhenSiteTypeDOOHWithBanner() {
    // given
    site.setType(Type.DOOH);
    position.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    position.setVideoSupport(VideoSupport.BANNER);

    // when & then
    assertDoesNotThrow(
        () ->
            ReflectionTestUtils.invokeMethod(
                publisherSiteAssembler, "checkPositionVideoSupport", site, position));
  }

  @Test
  void shouldThrowBadRequestWhenSellerCtvSellingIsDisabledAndSiteLevelPlatformIsSetCtvOtt() {
    // given
    var company = new Company();
    var sellerAttributes = new SellerAttributes();

    sellerAttributes.setEnableCtvSelling(false);
    company.setSellerAttributes(sellerAttributes);
    publisherSiteDto.setPlatform(PublisherSiteDTO.Platform.CTV_OTT);

    given(companyService.getCompany(anyLong())).willReturn(company);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSiteAssembler.apply(PUBLISHER_PID, site, publisherSiteDto));
    assertEquals(
        ServerErrorCodes.SERVER_SITE_PLATFORM_CTV_OTT_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldThrowSecurityExceptionOnSiteDcnMisMatch() {
    // given
    var site = new Site();
    site.setDcn("test.com");
    var publisherSiteDto = new PublisherSiteDTO();
    publisherSiteDto.setDcn("somethingelse.com");

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSiteAssembler.apply(PUBLISHER_PID, site, publisherSiteDto));
    assertEquals(ServerErrorCodes.SERVER_SITE_DCN_READONLY, exception.getErrorCode());
  }
}
