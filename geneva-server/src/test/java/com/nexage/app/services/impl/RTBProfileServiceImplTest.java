package com.nexage.app.services.impl;

import static com.nexage.admin.core.enums.AlterReserve.ALWAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.dto.RtbProfileTagHierarchyDto;
import com.nexage.admin.core.dto.TagHierarchyDto;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileAssignmentsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.assemblers.PublisherDefaultRTBProfileAssembler;
import com.nexage.app.util.assemblers.PublisherRTBProfileAssembler;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RTBProfileServiceImplTest {

  private static final boolean DETAILS = false;

  @Mock private LoginUserContext userContext;
  @Mock private PositionRepository positionRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private RTBProfileLibraryRepository rtbProfileLibraryRepository;
  @Mock private RTBProfileUtil rtbProfileUtil;
  @Mock private PublisherDefaultRTBProfileAssembler publisherDefaultRTBProfileAssembler;
  @Mock private TagRepository tagRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private CompanyService companyService;
  @Mock private SellerSiteService sellerSiteService;
  @Mock private SellerTagService sellerTagService;
  @Mock private EntityManager entityManager;
  @Mock private SiteRepository siteRepository;
  @InjectMocks private RTBProfileServiceImpl rtbProfileService;
  @InjectMocks private PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  @InjectMocks private TransparencyServiceImpl transparencyService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        rtbProfileService, "publisherRTBProfileAssembler", publisherRTBProfileAssembler);
    ReflectionTestUtils.setField(
        publisherRTBProfileAssembler, "transparencyService", transparencyService);
  }

  @Test
  void shouldReturnDbRtbProfileWhenExternalUserIsLoggedIn() {

    // Given
    Site site = new Site();

    RTBProfile dbRtbProfile = new RTBProfile();
    dbRtbProfile.setName("TestDbRtbProfile");

    Company company = new Company();

    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile = new PublisherDefaultRTBProfileDTO();

    when(userContext.isNexageAdminOrManager()).thenReturn(false);

    // When
    RTBProfile result =
        rtbProfileService.processDefaultRtbProfile(
            site, company, publisherDefaultRTBProfile, dbRtbProfile, DETAILS);

    // Then
    assertNotNull(result);
    assertEquals(result, dbRtbProfile);
  }

  @Test
  void shouldReturnPublisherDefaultRTBProfileWhenInternalUserIsLoggedIn() {

    // Given
    Site site = new Site();

    RTBProfile profile = new RTBProfile();
    profile.setName("TestRtbProfile");

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDefaultRtbProfile(profile);

    Company company = new Company();
    company.setId("TestId");
    company.setName("TestCompany");
    company.setSellerAttributes(sellerAttributes);

    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile = new PublisherDefaultRTBProfileDTO();
    publisherDefaultRTBProfile.setPid(1L);

    when(userContext.isNexageAdminOrManager()).thenReturn(true);
    when(rtbProfileRepository.findById(anyLong())).thenReturn(Optional.of(profile));
    when(publisherDefaultRTBProfileAssembler.apply(
            any(PublisherDefaultRTBProfileContext.class),
            any(RTBProfile.class),
            any(PublisherDefaultRTBProfileDTO.class),
            any(Boolean.class)))
        .thenReturn(profile);

    // When
    RTBProfile result =
        rtbProfileService.processDefaultRtbProfile(
            site, company, publisherDefaultRTBProfile, profile, DETAILS);

    // Then
    assertNotNull(result);
    assertEquals(result, profile);
  }

  @Test
  void shouldHaveSiteTypeCodeForWebsite() {
    // Given
    PublisherTagDTO publisherTagDTO = PublisherTagDTO.newBuilder().build();
    PublisherRTBProfileDTO rtbProfileDTO = PublisherRTBProfileDTO.newBuilder().build();
    rtbProfileDTO.setAlterReserve(ALWAYS);
    publisherTagDTO.setRtbProfile(rtbProfileDTO);
    Tag tag = new Tag();
    Site site = new Site();
    site.setType(Type.WEBSITE);
    Company company = new Company();
    company.setPid(123L);
    site.setCompany(company);

    when(userContext.isNexageUser()).thenReturn(true);

    // When
    RTBProfile rtbProfile = rtbProfileService.createTagRTBProfile(publisherTagDTO, tag, site);

    // Then
    assertEquals('S', rtbProfile.getSiteType());
  }

  @Test
  void shouldReturnTagHierarchyWithAllowlist() {
    TagHierarchyDto th = mock(TagHierarchyDto.class);
    RtbProfileTagHierarchyDto pph = mock(RtbProfileTagHierarchyDto.class);

    when(rtbProfileRepository.getAllTagHierarchy(anyLong())).thenReturn(List.of(pph));
    when(tagRepository.getTagHierarchy(anyLong(), anyLong(), anyLong())).thenReturn(Set.of(th));
    when(companyService.getCompany(anyLong())).thenReturn(new Company());
    when(sellerTagService.getTagRevenue(anyLong(), anyLong())).thenReturn(BigDecimal.ZERO);

    Set<PublisherHierarchyDTO> publisherHierarchyDTOS = rtbProfileService.getTagHierachy(1L, 1L);

    assertTrue(publisherHierarchyDTOS.size() > 0);
    publisherHierarchyDTOS.forEach(
        publisherHierarchyDTO -> {
          assertTrue(publisherHierarchyDTO.getSite().size() > 0);
          publisherHierarchyDTO
              .getSite()
              .forEach(
                  pubSiteHierarchyDTO -> {
                    assertTrue(pubSiteHierarchyDTO.getPositions().size() > 0);
                    pubSiteHierarchyDTO
                        .getPositions()
                        .forEach(
                            pubPositionHierarchy -> {
                              assertTrue(pubPositionHierarchy.getTags().size() > 0);
                              pubPositionHierarchy
                                  .getTags()
                                  .forEach(
                                      pubTagHierarchyDTO ->
                                          assertNotNull(
                                              pubTagHierarchyDTO.getFilterBiddersAllowlist()));
                            });
                  });
        });
  }

  @Test
  void shouldReturnNullCloneNullTagRTBProfile() {
    // Given
    PublisherTagDTO publisherTagDTO = PublisherTagDTO.newBuilder().build();
    PublisherRTBProfileDTO rtbProfileDTO = PublisherRTBProfileDTO.newBuilder().build();
    rtbProfileDTO.setAlterReserve(ALWAYS);
    publisherTagDTO.setRtbProfile(rtbProfileDTO);
    Tag originTag = new Tag();
    Site originSite = new Site();
    Tag newTag = new Tag();
    Site destinationSite = new Site();
    originSite.setType(Type.WEBSITE);
    Company company = new Company();
    company.setPid(123L);
    originSite.setCompany(company);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setExchangeSiteTagId("TestId");
    rtbProfile.setPid(1L);
    originSite.getRtbProfiles().add(rtbProfile);
    originTag.setPrimaryId(rtbProfile.getExchangeSiteTagId());
    when(rtbProfileRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When
    RTBProfile rtbProfileCloned =
        rtbProfileService.cloneTagRTBProfile(
            destinationSite, newTag, originSite, originTag, publisherTagDTO);
    rtbProfile.setPid(null);
    rtbProfile.setExchangeSiteTagId(null);
    rtbProfile.setVersion(null);

    // Then
    assertTrue((rtbProfileCloned == null));
  }

  @Test
  void shouldReturnClonedTagRTBProfileWhenClone() {
    // Given
    PublisherTagDTO publisherTagDTO = PublisherTagDTO.newBuilder().build();
    PublisherRTBProfileDTO rtbProfileDTO = PublisherRTBProfileDTO.newBuilder().build();
    rtbProfileDTO.setAlterReserve(ALWAYS);
    rtbProfileDTO.setLibraries(new HashSet<>());
    publisherTagDTO.setRtbProfile(rtbProfileDTO);
    Tag originTag = new Tag();
    Site originSite = new Site();
    Tag newTag = new Tag();
    Site destinationSite = new Site();
    destinationSite.setType(Type.WEBSITE);
    Company company = new Company();
    company.setPid(123L);
    destinationSite.setCompany(company);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.onCreate();
    rtbProfile.setExchangeSiteTagId("TestId");
    rtbProfile.setPid(1L);
    rtbProfile.setLibraryPids(new HashSet<>());
    rtbProfile.setSite(new Site());
    rtbProfile.setTag(new Tag());
    originSite.getRtbProfiles().add(rtbProfile);
    originTag.setPrimaryId(rtbProfile.getExchangeSiteTagId());

    when(userContext.isNexageUser()).thenReturn(true);
    when(rtbProfileRepository.findById(anyLong())).thenReturn(Optional.of(rtbProfile));

    // When
    RTBProfile rtbProfileCloned =
        rtbProfileService.cloneTagRTBProfile(
            destinationSite, newTag, originSite, originTag, publisherTagDTO);
    rtbProfile.setPid(null);
    rtbProfile.setExchangeSiteTagId(null);
    rtbProfile.setVersion(null);

    // Then
    assertEquals(rtbProfile.getExchangeSiteTagId(), rtbProfileCloned.getExchangeSiteTagId());
    assertNotSame(rtbProfile, rtbProfileCloned);
  }

  @Test
  void shouldAddRtbProfileLibraryAssociations() {
    // given
    long publisherPid = 123L;
    long tagPid = 456L;
    long rtbProfileLibraryPid = 789L;
    String primaryId = UUID.randomUUID().toString();

    RtbProfileLibsAndTagsDTO rtbProfileLibAndTagList =
        RtbProfileLibsAndTagsDTO.builder()
            .tagPid(List.of(tagPid))
            .rtbProfileLibPid(List.of(rtbProfileLibraryPid))
            .removedTagPid(List.of())
            .build();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(rtbProfileLibraryPid);

    RtbProfileLibrary unmatchedRtbProfileLibrary = new RtbProfileLibrary();
    unmatchedRtbProfileLibrary.setPid(rtbProfileLibraryPid + 1);

    RTBProfileLibraryAssociation unmatchedAssociation = new RTBProfileLibraryAssociation();
    unmatchedAssociation.setLibrary(unmatchedRtbProfileLibrary);

    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.getLibraries().add(unmatchedAssociation);

    given(rtbProfileLibraryRepository.findAllById(rtbProfileLibAndTagList.getRtbProfileLibPid()))
        .willReturn(List.of(rtbProfileLibrary));
    given(tagRepository.getPrimaryIdForPidIn(List.of(tagPid))).willReturn(List.of(primaryId));
    given(rtbProfileRepository.findByExchangeSiteTagIdIn(List.of(primaryId)))
        .willReturn(List.of(rtbProfile));

    // when
    rtbProfileService.updateRTBProfileLibToRTBProfilesMap(publisherPid, rtbProfileLibAndTagList);

    // then
    assertEquals(2, rtbProfile.getLibraries().size());
    assertTrue(rtbProfile.getLibraries().contains(unmatchedAssociation));

    RTBProfileLibraryAssociation addedAssociation =
        rtbProfile.getLibraries().stream()
            .filter(a -> !a.equals(unmatchedAssociation))
            .findFirst()
            .orElseThrow();
    assertSame(rtbProfileLibrary, addedAssociation.getLibrary());
    assertSame(rtbProfile, addedAssociation.getRtbprofile());
  }

  @Test
  void shouldNotAddDuplicateRtbProfileLibraryAssociations() {
    // given
    long publisherPid = 123L;
    long tagPid = 456L;
    long rtbProfileLibraryPid = 789L;
    String primaryId = UUID.randomUUID().toString();

    RtbProfileLibsAndTagsDTO rtbProfileLibAndTagList =
        RtbProfileLibsAndTagsDTO.builder()
            .tagPid(List.of(tagPid))
            .rtbProfileLibPid(List.of(rtbProfileLibraryPid))
            .removedTagPid(List.of())
            .build();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(rtbProfileLibraryPid);

    RTBProfileLibraryAssociation association = new RTBProfileLibraryAssociation();
    association.setLibrary(rtbProfileLibrary);

    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.getLibraries().add(association);

    given(rtbProfileLibraryRepository.findAllById(rtbProfileLibAndTagList.getRtbProfileLibPid()))
        .willReturn(List.of(rtbProfileLibrary));
    given(tagRepository.getPrimaryIdForPidIn(List.of(tagPid))).willReturn(List.of(primaryId));
    given(rtbProfileRepository.findByExchangeSiteTagIdIn(List.of(primaryId)))
        .willReturn(List.of(rtbProfile));

    // when
    rtbProfileService.updateRTBProfileLibToRTBProfilesMap(publisherPid, rtbProfileLibAndTagList);

    // then
    assertEquals(1, rtbProfile.getLibraries().size());
    assertTrue(rtbProfile.getLibraries().contains(association));
  }

  @Test
  void shouldRemoveRtbProfileLibraryAssociations() {
    // given
    long publisherPid = 123L;
    long removedTagPid = 456L;
    long rtbProfileLibraryPid = 789L;
    String primaryIdToRemove = UUID.randomUUID().toString();

    RtbProfileLibsAndTagsDTO rtbProfileLibAndTagList =
        RtbProfileLibsAndTagsDTO.builder()
            .tagPid(List.of())
            .rtbProfileLibPid(List.of(rtbProfileLibraryPid))
            .removedTagPid(List.of(removedTagPid))
            .build();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(rtbProfileLibraryPid);

    RTBProfileLibraryAssociation association = new RTBProfileLibraryAssociation();
    association.setLibrary(rtbProfileLibrary);

    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.getLibraries().add(association);

    given(rtbProfileLibraryRepository.findAllById(rtbProfileLibAndTagList.getRtbProfileLibPid()))
        .willReturn(List.of(rtbProfileLibrary));
    given(tagRepository.getPrimaryIdForPidIn(anyList())).willReturn(List.of(primaryIdToRemove));
    given(rtbProfileRepository.findByExchangeSiteTagIdIn(List.of(primaryIdToRemove)))
        .willReturn(List.of(rtbProfile));

    // when
    rtbProfileService.updateRTBProfileLibToRTBProfilesMap(publisherPid, rtbProfileLibAndTagList);

    // then
    assertEquals(0, rtbProfile.getLibraries().size());
  }

  @Test
  void shouldUpdateTagRTBProfile() {
    // given
    String oldName = "oldName";
    String newName = "newName";
    long rtbProfilePid = 1L;
    long companyPid = 2L;
    RTBProfile rtbProfile = buildRTBProfile(rtbProfilePid, oldName);
    Company company = new Company();
    company.setPid(companyPid);
    Site site = new Site();
    site.setRtbProfiles(Set.of(rtbProfile));
    site.setCompany(company);
    Tag tag = new Tag();
    PublisherTagDTO publisherTagDTO = new PublisherTagDTO();
    publisherTagDTO.setRtbProfile(buildRTBProfileDto(rtbProfilePid, newName));
    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));

    // when
    RTBProfile returnedRtbProfile =
        rtbProfileService.updateTagRTBProfile(site, tag, publisherTagDTO);

    // then
    assertEquals(newName, returnedRtbProfile.getName());
  }

  @Test
  void shouldReadDefaultProfile() {
    // Given
    Company company = new Company();
    company.setDefaultRtbProfilesEnabled(true);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setPid(2L);
    Site site = new Site();
    site.setStatus(Status.DELETED);
    Position position = new Position();
    position.setStatus(Status.DELETED);
    PublisherDefaultRTBProfileAssignmentsDTO publisherDefaultRTBProfileAssignmentsDTO =
        PublisherDefaultRTBProfileAssignmentsDTO.newBuilder().build();
    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfileDTO =
        PublisherDefaultRTBProfileDTO.newBuilder()
            .withDefaultRtbProfileOwnerCompanyPid(1L)
            .withNumberOfEffectivePlacements(1L)
            .withRtbProfileAssignments(publisherDefaultRTBProfileAssignmentsDTO)
            .build();

    given(companyService.getCompany(1L)).willReturn(company);
    given(rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndPid(anyLong(), anyLong()))
        .willReturn(Optional.of(rtbProfile));
    given(
            publisherDefaultRTBProfileAssembler.make(
                any(PublisherDefaultRTBProfileContext.class), any(RTBProfile.class), anySet()))
        .willReturn(publisherDefaultRTBProfileDTO);

    // When
    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile =
        rtbProfileService.readDefaultRTBProfile(1L, 2L);

    // Then
    assertEquals(1L, publisherDefaultRTBProfile.getDefaultRtbProfileOwnerCompanyPid());
    assertEquals(1L, publisherDefaultRTBProfile.getNumberOfEffectivePlacements());
  }

  @Test
  void shouldCreateProfile() {
    // Given
    Company company = new Company();
    company.setDefaultRtbProfilesEnabled(true);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setPid(99L);
    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfileDTO =
        PublisherDefaultRTBProfileDTO.newBuilder()
            .withDefaultRtbProfileOwnerCompanyPid(1L)
            .withName("ABC")
            .withPid(99L)
            .build();

    given(companyService.getCompany(anyLong())).willReturn(company);
    given(
            publisherDefaultRTBProfileAssembler.apply(
                any(PublisherDefaultRTBProfileContext.class),
                any(RTBProfile.class),
                any(PublisherDefaultRTBProfileDTO.class)))
        .willReturn(rtbProfile);
    given(
            rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndName(
                anyLong(), anyString()))
        .willReturn(List.of(rtbProfile));
    given(rtbProfileRepository.save(any(RTBProfile.class))).willReturn(rtbProfile);
    given(rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndPid(anyLong(), anyLong()))
        .willReturn(Optional.of(rtbProfile));
    given(
            publisherDefaultRTBProfileAssembler.make(
                any(PublisherDefaultRTBProfileContext.class), any(RTBProfile.class), anySet()))
        .willReturn(publisherDefaultRTBProfileDTO);

    // When
    PublisherDefaultRTBProfileDTO result =
        rtbProfileService.createDefaultRTBProfile(1L, publisherDefaultRTBProfileDTO);

    // Then
    assertEquals(99L, result.getPid());
  }

  @Test
  void shouldProcessAndReturnCompanyDefaultProfileWhenNotNull() {
    // Given
    var tagDTO = PublisherTagDTO.newBuilder().withPid(1L).build();
    var company = new Company();
    company.setPid(1L);

    var publisherPid = 100L;
    PublisherDefaultRTBProfileDTO publisherDefaultRTBProfileDTO =
        PublisherDefaultRTBProfileDTO.newBuilder()
            .withPid(1L)
            .withName("ABC")
            .withTag(tagDTO)
            .withVersion(1)
            .build();
    var dbRtbProfile = new RTBProfile();
    var tag = new Tag();
    tag.setPid(1L);
    dbRtbProfile.setTag(tag);

    var profile = new RTBProfile();
    profile.setVersion(1);
    profile.setOwnerCompany(company);

    given(userContext.isNexageAdminOrManager()).willReturn(true);
    given(rtbProfileRepository.findById(anyLong())).willReturn(Optional.of(new RTBProfile()));
    given(
            publisherDefaultRTBProfileAssembler.apply(
                any(PublisherDefaultRTBProfileContext.class),
                any(RTBProfile.class),
                any(PublisherDefaultRTBProfileDTO.class)))
        .willReturn(profile);

    // When
    var result =
        rtbProfileService.processCompanyDefaultRtbProfile(
            publisherPid, publisherDefaultRTBProfileDTO, dbRtbProfile, company);

    // Then
    assertNotNull(result);
    verify(publisherDefaultRTBProfileAssembler)
        .apply(
            any(PublisherDefaultRTBProfileContext.class),
            any(RTBProfile.class),
            any(PublisherDefaultRTBProfileDTO.class));
  }

  @Test
  void shouldThrowUnauthorizedWhenDefaultRtbProfileIsNull() {
    var rtbProfile = new RTBProfile();
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> rtbProfileService.processCompanyDefaultRtbProfile(1L, null, rtbProfile, null));
    assertEquals(
        ServerErrorCodes.SERVER_NOT_AUTHORIZED_FOR_DEFAULT_RTB_PROFILE, exception.getErrorCode());
  }

  @Test
  void shouldThrowUnauthorizedIfNotPerformedByNexageAdmin() {
    var publisherDefaultRTBProfileDTO = new PublisherDefaultRTBProfileDTO();
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () ->
                rtbProfileService.processCompanyDefaultRtbProfile(
                    null, publisherDefaultRTBProfileDTO, null, null));
    assertEquals(
        ServerErrorCodes.SERVER_NOT_AUTHORIZED_FOR_DEFAULT_RTB_PROFILE, exception.getErrorCode());
  }

  private RTBProfile buildRTBProfile(long pid, String name) {
    var rtbProfile = new RTBProfile();
    rtbProfile.setPid(pid);
    rtbProfile.setName(name);
    rtbProfile.setVersion(1);
    return rtbProfile;
  }

  private PublisherRTBProfileDTO buildRTBProfileDto(long pid, String name) {
    var rtbProfileDto = new PublisherRTBProfileDTO();
    rtbProfileDto.setPid(pid);
    rtbProfileDto.setName(name);
    rtbProfileDto.setVersion(1);
    return rtbProfileDto;
  }
}
