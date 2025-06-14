package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherImpressionGroupDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.TransparencyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherSiteAssemblerCreateAndUpdateTest {

  @Mock CompanyService companyService;
  @Mock Site site;
  Long PUBLISHER_PID = 123L;
  Long PUBLISHER_SITE_PID = 1001L;
  @InjectMocks private PublisherSiteAssembler assembler;
  @Mock private UserContext userContext;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private TransparencyService transparencyService;

  @BeforeEach
  public void setup() {
    site = createSite();
  }

  @Test
  void shouldProperlySetImpressionGroupWhenNotNullAndNexageUser() {
    // given
    var company = new Company();
    var sellerAttributes = new SellerAttributes();
    company.setSellerAttributes(sellerAttributes);
    given(companyService.getCompany(anyLong())).willReturn(company);
    site.setPid(null);
    given(userContext.isNexageUser()).willReturn(true);
    PublisherSiteDTO publisherSite =
        createPublisherSiteDTODefault()
            .withImpressionGroup(new PublisherImpressionGroupDTO(true, Set.of("group1")))
            .withPid(null)
            .build();

    // when
    site = assembler.apply(PUBLISHER_PID, site, publisherSite);

    // then
    assertTrue(site.getImpressionGroup().isEnabled());
    assertEquals(1, site.getImpressionGroup().getGroups().size());
    assertTrue(site.getImpressionGroup().getGroups().contains("group1"));
  }

  @Test
  void shouldProperlySetAllDtoValues() {
    // given
    setAllFieldsForSite();

    // when
    PublisherSiteDTO dto =
        assembler.make(site, PublisherSiteAssembler.ALL_FIELDS_NEXAGE_ADMIN_MANAGER, false);

    // then
    assertEquals(PUBLISHER_PID, dto.getPid());
    assertEquals(3, dto.getVersion());
    assertEquals("dcn", dto.getDcn());
    assertEquals("domain", dto.getDomain());
    assertEquals("description", dto.getDescription());
    assertEquals("name", dto.getName());
    assertEquals(PublisherSiteDTO.Platform.ANDROID, dto.getPlatform());
    assertEquals(Status.ACTIVE, dto.getStatus());
    assertEquals(PublisherSiteDTO.SiteType.DESKTOP, dto.getType());
    assertEquals("url", dto.getUrl());
    assertEquals("appBundle", dto.getAppBundle());
    assertTrue(dto.isCoppaRestricted());
    assertEquals("rtb1CategoryRollup", dto.getRtb1CategoryRollup());
    assertEquals(1, dto.getIabCategories().size());
    assertTrue(dto.getIabCategories().contains("iabCategory"));
    assertEquals(PublisherSiteDTO.Mode.LIVE, dto.getMode());
    assertTrue(dto.isHbEnabled());
    assertEquals(1, dto.getTrafficThrottle());
    assertTrue(dto.isAdTruthEnabled());
    assertTrue(dto.getMetadataEnablement());
    assertEquals("globalAliasName", dto.getGlobalAliasName());
    assertTrue(dto.getImpressionGroup().isEnabled());
    assertEquals(1, dto.getImpressionGroup().getGroups().size());
    assertTrue(dto.getImpressionGroup().getGroups().contains("group1"));
    assertEquals(11, dto.getReportFrequency());
    assertEquals(22, dto.getReportBatchSize());
    assertEquals(33, dto.getRulesUpdateFrequency());
    assertTrue(dto.isFilterBots());
    assertEquals(44, dto.getBuyerTimeout());
    assertEquals(55, dto.getDaysFree());
    assertEquals(66, dto.getTotalTimeout());
    assertEquals(1, dto.getDefaultPositions().size());
    assertTrue(dto.getDefaultPositions().contains("defaultPosition"));
    assertEquals(1, dto.getPassthruParameters().size());
    assertTrue(dto.getPassthruParameters().contains("passthruParameter"));
    assertTrue(dto.isConsumerProfileContributed());
    assertTrue(dto.isConsumerProfileUsed());
    assertTrue(dto.isOverrideIP());
    assertEquals("ethnicityMap", dto.getEthnicityMap());
    assertEquals("genderMap", dto.getGenderMap());
    assertEquals("maritalStatusMap", dto.getMaritalStatusMap());
    assertEquals("inputDateFormat", dto.getInputDateFormat());
  }

  @Test
  void shouldCallMakeUsingAllFieldsForNexageUser() {
    // given
    setAllFieldsForSite();
    given(userContext.isNexageUser()).willReturn(true);
    given(userContext.isNexageAdminOrManager()).willReturn(false);

    // when
    PublisherSiteDTO dto = assembler.make(site, false);

    // then
    assertEquals(PUBLISHER_PID, dto.getPid());
    assertEquals("ethnicityMap", dto.getEthnicityMap());
  }

  @Test
  void shouldCallMakeUsingAllFieldsForNexageAdminOrManager() {
    // given
    setAllFieldsForSite();
    given(userContext.isNexageUser()).willReturn(true);
    given(userContext.isNexageAdminOrManager()).willReturn(true);

    // when
    PublisherSiteDTO dto = assembler.make(site, false);

    // then
    assertEquals(PUBLISHER_PID, dto.getPid());
    assertEquals("ethnicityMap", dto.getEthnicityMap());
  }

  @Test
  void shouldCallMakeUsingDefaultFieldsForNonNexageUser() {
    // given
    setAllFieldsForSite();
    given(userContext.isNexageUser()).willReturn(false);

    // when
    PublisherSiteDTO dto = assembler.make(site, false);

    // then
    assertEquals(PUBLISHER_PID, dto.getPid());
    assertNull(dto.getEthnicityMap());
  }

  @Test
  void shouldThrowWhenCreativeSuccessRateThresholdInvalidValue() {
    PublisherSiteDTO publisherSite =
        createPublisherSiteDTODefault()
            .withCreativeSuccessRateThreshold(new BigDecimal(110))
            .build();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(PUBLISHER_PID, site, publisherSite));

    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldReturnSuccessWhenMakeCreativeSuccessRateThresholdWithValidValue() {
    final Set<String> fields = Set.of("creativeSuccessRateThreshold");
    site.setCreativeSuccessRateThreshold(new BigDecimal(50.5));

    PublisherSiteDTO dto = assembler.make(site, fields, false);

    assertEquals(50.5f, dto.getCreativeSuccessRateThreshold().floatValue());
  }

  @Test
  void
      shouldReturnSiteWithPlatformCtvOttWhenSellerCtvSellingIsEnabledAndSitePlatformIsSetToCtvOtt() {
    // given
    var company = new Company();
    var sellerAttributes = new SellerAttributes();
    sellerAttributes.setEnableCtvSelling(true);
    company.setSellerAttributes(sellerAttributes);
    when(companyService.getCompany(anyLong())).thenReturn(company);
    PublisherSiteDTO publisherSite =
        createPublisherSiteDTODefault().withPlatform(PublisherSiteDTO.Platform.CTV_OTT).build();

    // when
    Site assembledSite = assembler.apply(PUBLISHER_PID, site, publisherSite);

    // then
    assertEquals(PublisherSiteDTO.Platform.CTV_OTT.name(), assembledSite.getPlatform().name());
  }

  PublisherSiteDTO.Builder createPublisherSiteDTODefault() {
    return new PublisherSiteDTO()
        .newBuilder()
        .withPid(PUBLISHER_SITE_PID)
        .withType(PublisherSiteDTO.SiteType.MOBILE_WEB)
        .withPlatform(PublisherSiteDTO.Platform.ANDROID)
        .withMode(PublisherSiteDTO.Mode.TEST)
        .withStatus(Status.ACTIVE);
  }

  Site createSite() {
    Site site = new Site();
    site.setPid(PUBLISHER_PID);

    return site;
  }

  void setAllFieldsForSite() {
    site.setVersion(3);
    site.setDcn("dcn");
    site.setDomain("domain");
    site.setDescription("description");
    site.setName("name");
    site.setPlatform(Platform.ANDROID);
    site.setStatus(com.nexage.admin.core.enums.Status.ACTIVE);
    site.setType(Type.DESKTOP);
    site.setUrl("url");
    site.setAppBundle("appBundle");
    site.setCoppaRestricted(true);
    site.setRtb1CategoryRollup("rtb1CategoryRollup");
    site.setIabCategories(Set.of("iabCategory"));
    site.setLive(true);
    site.setHbEnabled(true);
    site.setTrafficThrottle(1);
    site.setAdTruthEnabled(true);
    site.setMetadataEnablement(true);
    site.setGlobalAliasName("globalAliasName");
    site.setImpressionGroup(new Site.ImpressionGroup(true, Set.of("group1")));
    site.setReportFrequency(11);
    site.setReportBatchSize(22);
    site.setRulesUpdateFrequency(33);
    site.setFilterBots(true);
    site.setBuyerTimeout(44);
    site.setDaysFree(55);
    site.setTotalTimeout(66);
    site.setDefaultPositions(Set.of("defaultPosition"));
    site.setPassthruParameters(Set.of("passthruParameter"));
    site.setConsumerProfileContributed(true);
    site.setConsumerProfileUsed(true);
    site.setOverrideIP(true);
    site.setEthnicityMap("ethnicityMap");
    site.setGenderMap("genderMap");
    site.setMaritalStatusMap("maritalStatusMap");
    site.setInputDateFormat("inputDateFormat");
    site.setIncludeSiteName(2);
    site.setSiteNameAlias("siteNameAlias");
    site.setSiteAliasId(77L);
    site.setIncludePubName(1);
    site.setPubNameAlias("pubNameAlias");
    site.setPubAliasId(88L);
  }
}
