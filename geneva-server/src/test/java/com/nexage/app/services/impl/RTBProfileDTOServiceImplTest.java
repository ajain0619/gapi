package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RTBProfile.ScreeningLevel;
import com.nexage.admin.core.model.RTBProfileView;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.app.dto.RTBProfileDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.RTBProfileValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class RTBProfileDTOServiceImplTest {

  @Mock RTBProfileRepository rtbProfileRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private RTBProfileValidator rtbProfileValidator;
  @InjectMocks RTBProfileDTOServiceImpl rtbProfileDTOService;

  @Test
  void shouldGetDefaultRTBProfiles() {
    Pageable pageable = PageRequest.of(0, 1);

    Page<RTBProfileView> page = new PageImpl(List.of(createRTBProfileView()));

    when(companyRepository.existsById(anyLong())).thenReturn(true);
    when(rtbProfileRepository.getDefaultRTBProfileBySellerPid(anyLong())).thenReturn(1L);
    when(positionRepository.count(any(Specification.class))).thenReturn(5L);
    when(rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndNameLike(
            anyLong(), anyString(), any(Pageable.class)))
        .thenReturn(page);
    Page<RTBProfileDTO> defaultRTBProfileDTOList =
        rtbProfileDTOService.getRTBProfiles(pageable, 1L, "3", Set.of("name"));
    verify(companyRepository, atLeastOnce()).existsById(anyLong());
    verify(rtbProfileRepository, atLeastOnce()).getDefaultRTBProfileBySellerPid(anyLong());
    verify(positionRepository, atLeastOnce()).count(any(Specification.class));
    assertTrue(defaultRTBProfileDTOList.get().findFirst().get().isPublisherDefault());
    assertEquals(
        5L,
        defaultRTBProfileDTOList
            .get()
            .findFirst()
            .get()
            .getNumberOfEffectivePlacements()
            .longValue());
    assertEquals("Test", defaultRTBProfileDTOList.get().findFirst().get().getName());
  }

  @Test
  void shouldThrowExceptionWhenGettingRtbProfilesForNonexistentCompanyPid() {
    Pageable pageable = PageRequest.of(0, 1);
    Set<String> qf = Set.of("name");
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.getRTBProfiles(pageable, 1L, "3", qf));

    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGettingRtbProfilesForSellerWithoutRtbProfile() {
    Pageable pageable = PageRequest.of(0, 1);
    Set<String> qf = Set.of("name");
    Company company = new Company();
    SellerAttributes sellerAttributes = new SellerAttributes();
    company.setSellerAttributes(sellerAttributes);

    when(companyRepository.existsById(anyLong())).thenReturn(true);
    when(rtbProfileRepository.getDefaultRTBProfileBySellerPid(anyLong())).thenReturn(null);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.getRTBProfiles(pageable, 1L, "3", qf));

    assertEquals(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGettingRtbProfilesForInvalidQueryField() {
    Pageable pageable = PageRequest.of(0, 1);
    Set<String> qf = Set.of("whoknows");
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.getRTBProfiles(pageable, 123L, "3", qf));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldUpdateRTBProfiles() {

    RTBProfileDTO dtoObject = new RTBProfileDTO();
    dtoObject.setVersion(1);
    dtoObject.setName("Updated Name");
    Optional<RTBProfile> rtbProfile = Optional.of(new RTBProfile());
    rtbProfile.get().setVersion(1);
    rtbProfile.get().setDefaultRtbProfileOwnerCompanyPid(1L);
    when(rtbProfileRepository.findById(anyLong())).thenReturn(rtbProfile);
    rtbProfileDTOService.update(1L, dtoObject, 1L);
    assertNotNull(rtbProfile);
    assertEquals(rtbProfile.get().getName(), dtoObject.getName());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingRtbProfilesForProfileBelongingToDifferentSeller() {

    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND))
        .when(rtbProfileValidator)
        .validateUpdate(anyLong(), any(Optional.class));
    var rtbProfileDto = new RTBProfileDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.update(1L, rtbProfileDto, 1L));
    assertEquals(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingRtbProfilesForCompanyWithDefaultProfilesNotEnabled() {

    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY))
        .when(rtbProfileValidator)
        .validateUpdate(anyLong(), any(Optional.class));
    var rtbProfileDto = new RTBProfileDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.update(1L, rtbProfileDto, 1L));
    assertEquals(
        ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingRtbProfilesForNonexistentProfile() {

    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND))
        .when(rtbProfileValidator)
        .validateUpdate(anyLong(), any(Optional.class));
    var rtbProfileDto = new RTBProfileDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileDTOService.update(1L, rtbProfileDto, 1L));
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND, exception.getErrorCode());
  }

  private RTBProfileView createRTBProfileView() {
    return new RTBProfileView() {

      @Override
      public String getName() {
        return "Test";
      }

      @Override
      public Long getPid() {
        return 1L;
      }

      @Override
      public int getAuctionType() {
        return 0;
      }

      @Override
      public String getBlockedAdTypes() {
        return null;
      }

      @Override
      public BigDecimal getPubNetLowReserve() {
        return null;
      }

      @Override
      public BigDecimal getPubNetReserve() {
        return null;
      }

      @Override
      public ScreeningLevel getScreeningLevel() {
        return null;
      }

      @Override
      public Integer getVersion() {
        return 1;
      }

      @Override
      public AlterReserve getAlterReserve() {
        return null;
      }

      @Override
      public BigDecimal getDefaultReserve() {
        return null;
      }

      @Override
      public boolean getIncludeConsumerId() {
        return false;
      }

      @Override
      public boolean getIncludeConsumerProfile() {
        return false;
      }

      @Override
      public boolean getIncludeDomainReferences() {
        return false;
      }

      @Override
      public boolean getIncludeGeoData() {
        return false;
      }

      @Override
      public BigDecimal getLowReserve() {
        return null;
      }

      @Override
      public Date getCreationDate() {
        return null;
      }

      @Override
      public String getDescription() {
        return null;
      }

      @Override
      public Date getLastUpdate() {
        return null;
      }
    };
  }
}
