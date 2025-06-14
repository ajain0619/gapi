package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.util.assemblers.context.PublisherRTBProfileContext;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherRTBProfileAssemblerTest {

  @Mock private PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private TransparencyService transparencyService;
  @Mock private UserContext userContext;

  @Spy private RTBProfile rtbProfile;

  @InjectMocks private PublisherRTBProfileAssembler publisherRTBProfileAssembler;

  @Test
  void nexageUserSetIncludeGeoDataTrue() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder().withIncludeGeoData(true).build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertTrue(rtbProfile.isIncludeGeoData());
  }

  @Test
  void nexageUserSetIncludeGeoDataFalse() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder().withIncludeGeoData(false).build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertFalse(rtbProfile.isIncludeGeoData());
  }

  @Test
  void nexageUserSetIncludeGeoDataNull() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder().withIncludeGeoData(null).build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertFalse(rtbProfile.isIncludeGeoData());
  }

  @Test
  void whenBidderFilterWhitelistOrBidderFilterAllowlistAreNotSet() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile = PublisherRTBProfileDTO.newBuilder().build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void whenOnlyBidderFilterWhitelistIsSet() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterWhitelist(false).build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void whenOnlyBidderFilterAllowlistIsSet() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterAllowlist(true).build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void whenBothBidderFilterWhitelistAndBidderFilterAllowlistAreSet() {
    when(userContext.isNexageUser()).thenReturn(true);
    Site site = new Site();
    Company company = new Company();
    site.setCompanyPid(1L);
    site.setCompany(company);
    PublisherRTBProfileDTO publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(false)
            .withBidderFilterAllowlist(true)
            .build();
    RTBProfile rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    // precedence is given to inclusive term
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
    publisherRTBProfile =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(true)
            .withBidderFilterAllowlist(false)
            .build();
    rtbProfile =
        publisherRTBProfileAssembler.apply(
            PublisherRTBProfileContext.newBuilder().withSite(site).build(),
            new RTBProfile(),
            publisherRTBProfile);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void makeRTBProfileBidderFilterAllowlistFalse() {
    final Set<String> fields = Set.of("bidderFilterAllowlist");

    rtbProfile.setBiddersFilterAllowlist(false);

    PublisherRTBProfileDTO returnedAttributes =
        publisherRTBProfileAssembler.make(new PublisherRTBProfileContext(), rtbProfile, fields);

    assertEquals(Boolean.FALSE, returnedAttributes.getBidderFilterAllowlist());
  }

  @Test
  void makeRTBProfileBidderFilterAllowlistTrue() {
    final Set<String> fields = Set.of("bidderFilterAllowlist");

    rtbProfile.setBiddersFilterAllowlist(true);

    PublisherRTBProfileDTO returnedAttributes =
        publisherRTBProfileAssembler.make(new PublisherRTBProfileContext(), rtbProfile, fields);

    assertEquals(Boolean.TRUE, returnedAttributes.getBidderFilterAllowlist());
  }

  @Test
  void shouldMakeWithConsumerIdAndDomainReferencesAndConsumerProfile() {
    // Given
    final Set<String> fields =
        Set.of("includeConsumerId", "includeDomainReferences", "includeConsumerProfile");

    rtbProfile.setIncludeConsumerId(true);
    rtbProfile.setIncludeDomainReferences(true);
    rtbProfile.setIncludeConsumerProfile(true);

    // When
    PublisherRTBProfileDTO returnedAttributes =
        publisherRTBProfileAssembler.make(new PublisherRTBProfileContext(), rtbProfile, fields);

    // Then
    assertEquals(Boolean.TRUE, returnedAttributes.getIncludeConsumerId());
    assertEquals(Boolean.TRUE, returnedAttributes.getIncludeDomainReferences());
    assertEquals(Boolean.TRUE, returnedAttributes.getIncludeConsumerProfile());
  }
}
