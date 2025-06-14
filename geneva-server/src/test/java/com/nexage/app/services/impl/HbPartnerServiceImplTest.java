package com.nexage.app.services.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.HbPartnerRequestDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.HbPartnerAssembler;
import com.nexage.app.util.validator.HbPartnerValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.PersistentObjectException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class HbPartnerServiceImplTest {

  private Page<HbPartner> pagedEntity;

  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private HbPartnerAssembler hbPartnerAssembler;
  @Mock private Pageable page;
  @Mock private UserContext userContext;
  @Mock private HbPartnerValidator hbPartnerValidator;
  @Mock private CompanyRepository companyRepository;
  @InjectMocks private HbPartnerServiceImpl hbPartnerService;

  @Test
  void shouldGetHbPartners() {
    pagedEntity = new PageImpl(TestObjectsFactory.gimme(10, HbPartner.class));
    when(hbPartnerRepository.findAll(any(Pageable.class))).thenReturn(pagedEntity);
    when(userContext.isNexageUser()).thenReturn(true);

    Page<HbPartnerDTO> hbPartnerDTOS =
        hbPartnerService.getHbPartners(HbPartnerRequestDTO.of(page, null, null, true));
    assertEquals(10, hbPartnerDTOS.getTotalElements(), "Incorrect count");
  }

  @Test
  void shouldThrowNotFoundExceptionWhenGetHbPartnerWithInvalidPid() {
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
    var ex = assertThrows(GenevaValidationException.class, () -> hbPartnerService.getHbPartner(1l));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldReturnNullWhenCreateHbPartnerWithoutName() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setName(null);
    HbPartnerDTO hbPartnerDTO = hbPartnerService.createHbPartner(hbPartnerDTOs.get(0));
    assertNull(hbPartnerDTO);
  }

  @Test
  void shouldReturnNullWhenCreateHbPartnerWithoutId() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setId(null);
    HbPartnerDTO hbPartnerDTO = hbPartnerService.createHbPartner(hbPartnerDTOs.get(0));
    assertNull(hbPartnerDTO);
  }

  @Test
  void shouldReturnNullWhenCreateHbPartnerWithoutStatus() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setStatus(null);
    HbPartnerDTO hbPartnerDTO = hbPartnerService.createHbPartner(hbPartnerDTOs.get(0));
    assertNull(hbPartnerDTO);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenUpdateHbPartnerWithInvalidPid() throws Exception {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> hbPartnerService.updateHbPartner(hbPartnerDTO));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowStaleStateExceptionWhenUpdateHbPartnerWithVersionMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(1);
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.of(hbPartner));
    doThrow(StaleStateException.class).when(hbPartnerValidator).isValidForUpdate(any(), any());
    assertThrows(StaleStateException.class, () -> hbPartnerService.updateHbPartner(hbPartnerDTO));
  }

  @Test
  void shouldThrowPersistentObjectExceptionWhenUpdateHbPartnerFail() throws Exception {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(1);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(1);
    hbPartner.setId("abc_123");
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.of(hbPartner));
    when(hbPartnerRepository.saveAndFlush(any(HbPartner.class)))
        .thenThrow(PersistentObjectException.class);

    assertThrows(
        PersistentObjectException.class, () -> hbPartnerService.updateHbPartner(hbPartnerDTO));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenDeactivateHbPartnerWithInvalidId() {
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.empty());
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> hbPartnerService.deactivateHbPartner(1L));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnDeactivateIfAssociatedToAnyCompany() {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(1L);
    when(hbPartnerRepository.findById(any(Long.class))).thenReturn(Optional.of(hbPartner));
    when(companyRepository.countCompaniesAssociatedToHbPartners(any())).thenReturn(1);
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> hbPartnerService.deactivateHbPartner(1L));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_DELETE_INVALID, ex.getErrorCode());
  }

  @Test
  void shouldGetAllHbPartners() {
    List<HbPartner> hbPartners = TestObjectsFactory.createHbPartners();
    when(hbPartnerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(hbPartners));
    when(hbPartnerAssembler.make(any(HbPartner.class))).thenCallRealMethod();
    when(userContext.isNexageUser()).thenReturn(true);
    when(hbPartnerAssembler.make(any(HbPartner.class), anyObject())).thenCallRealMethod();

    Page<HbPartnerDTO> hbPartnerDTOs =
        hbPartnerService.getHbPartners(
            HbPartnerRequestDTO.of(PageRequest.of(0, 10), null, null, true));
    assertEquals(hbPartners.size(), hbPartnerDTOs.getTotalElements(), "Incorrect count");

    hbPartnerDTOs
        .getContent()
        .forEach(
            partner ->
                assertThat(
                    hbPartners,
                    hasItem(
                        both(hasProperty("pid", equalTo(partner.getPid())))
                            .and(hasProperty("name", equalTo(partner.getName()))))));
  }

  @Test
  void shouldGetAllHbPartnersForSeller() {
    List<HbPartnersAssociationView> defaultSitesPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartners();
    List<HbPartner> partners = TestObjectsFactory.createHbPartners();
    when(siteRepository.findDefaultSitesPerPartners(anyLong())).thenReturn(defaultSitesPerPartner);
    lenient()
        .when(hbPartnerRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(partners));
    when(userContext.doSameOrNexageAffiliation(anyLong())).thenReturn(true);
    when(hbPartnerAssembler.make(any(HbPartner.class), anyObject())).thenCallRealMethod();

    Page<HbPartnerDTO> partnerDTOS =
        hbPartnerService.getHbPartners(
            HbPartnerRequestDTO.of(PageRequest.of(0, 10), 123L, null, false));
    assertEquals(partners.size(), partnerDTOS.getTotalElements(), "Incorrect count");

    partnerDTOS.forEach(
        partner ->
            assertThat(
                partners,
                hasItem(
                    both(hasProperty("pid", equalTo(partner.getPid())))
                        .and(hasProperty("name", equalTo(partner.getName()))))));

    defaultSitesPerPartner.forEach(
        d ->
            assertThat(
                partnerDTOS,
                hasItem(
                    both(hasProperty("pid", equalTo(d.getHbPartnerPid())))
                        .and(hasProperty("defaultSite", equalTo(d.getPid()))))));
  }

  @Test
  void shouldGetHbPartnersForSite() {
    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartners();
    List<HbPartner> partners = TestObjectsFactory.createHbPartners();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);
    when(hbPartnerRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(partners));
    when(userContext.canAccessSite(anyLong())).thenReturn(true);
    when(hbPartnerAssembler.make(any(HbPartner.class), anyObject())).thenCallRealMethod();

    Page<HbPartnerDTO> partnerDTOS =
        hbPartnerService.getHbPartners(
            HbPartnerRequestDTO.of(PageRequest.of(0, 10), 123L, 123L, false));
    assertEquals(partners.size(), partnerDTOS.getTotalElements(), "Incorrect count");

    partnerDTOS.forEach(
        partner ->
            assertThat(
                partners,
                hasItem(
                    both(hasProperty("pid", equalTo(partner.getPid())))
                        .and(hasProperty("name", equalTo(partner.getName()))))));

    defaultPositionsPerPartner.forEach(
        d -> {
          if (d.getType() == AssociationType.DEFAULT.ordinal()) {
            assertThat(
                partnerDTOS,
                hasItem(
                    both(hasProperty("pid", equalTo(d.getHbPartnerPid())))
                        .and(hasProperty("defaultPlacement", equalTo(d.getPid())))));
          } else if (d.getType() == AssociationType.DEFAULT_BANNER.ordinal()) {
            assertThat(
                partnerDTOS,
                hasItem(
                    both(hasProperty("pid", equalTo(d.getHbPartnerPid())))
                        .and(hasProperty("bannerDefaultPlacement", equalTo(d.getPid())))));
          } else if (d.getType() == AssociationType.DEFAULT_VIDEO.ordinal()) {
            assertThat(
                partnerDTOS,
                hasItem(
                    both(hasProperty("pid", equalTo(d.getHbPartnerPid())))
                        .and(hasProperty("videoDefaultPlacement", equalTo(d.getPid())))));
          }
        });
  }

  @Test
  void shouldThrowUserNotAuthorizedExceptionWhenGetAllHbPartnersWithUnAuthorizedUser() {
    List<HbPartner> hbPartners = TestObjectsFactory.createHbPartners();
    when(userContext.isNexageUser()).thenReturn(false);
    HbPartnerRequestDTO hbPartnerRequestDTO =
        HbPartnerRequestDTO.of(PageRequest.of(0, 10), null, null, true);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> hbPartnerService.getHbPartners(hbPartnerRequestDTO));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowUserNotAuthorizedExceptionWhenGetHbPartnersForSellerWithUnAuthorizedUser() {
    List<HbPartnersAssociationView> defaultSitesPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartners();
    List<HbPartner> partners = TestObjectsFactory.createHbPartners();
    lenient().when(userContext.doSameOrNexageAffiliation(456L)).thenReturn(true);
    HbPartnerRequestDTO hbPartnerRequestDTO =
        HbPartnerRequestDTO.of(PageRequest.of(0, 10), 123L, null, false);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> hbPartnerService.getHbPartners(hbPartnerRequestDTO));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowUserNotAuthorizedExceptionWhenGetHbPartnersForSiteWithUnAuthorizedUser() {
    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartners();
    List<HbPartner> partners = TestObjectsFactory.createHbPartners();
    lenient().when(userContext.canAccessSite(456L)).thenReturn(true);
    HbPartnerRequestDTO hbPartnerRequestDTO =
        HbPartnerRequestDTO.of(PageRequest.of(0, 10), 123L, 123L, false);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> hbPartnerService.getHbPartners(hbPartnerRequestDTO));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldFindPidsByCompanyPid() {
    // given
    List<Long> companyPids = new ArrayList<Long>(asList(1L, 2L));
    when(hbPartnerRepository.findPidsByCompanyPid(anyLong())).thenReturn(companyPids);

    // when
    List<Long> returnList = hbPartnerService.findPidsByCompanyPid(1L);

    // then
    assertTrue(returnList.containsAll(companyPids));
  }
}
