package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Validator;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SellerAttributesDTOServiceImplTest {

  @InjectMocks private SellerAttributesDTOServiceImpl sellerAttributesDTOService;
  @Mock private SellerAttributesRepository sellerAttributesRepo;
  @Mock private Validator validator;
  @Mock private LoginUserContext userContext;

  private Long sellerPid;

  @BeforeEach
  public void setUp() {
    sellerPid = RandomUtils.nextLong();
    lenient()
        .when(validator.validate(any(SellerAttributesDTO.class), any()))
        .thenReturn(Collections.emptySet());
  }

  private void setupUserContext(User.Role role) {
    SpringUserDetails loggedUserDetails = mock(SpringUserDetails.class);
  }

  @Test
  void getSellerAttributesDTO_validSeller_returnSellerAttributes() {

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setAdStrictApproval(false);

    Pageable pageable = PageRequest.of(0, 10);

    Page<SellerAttributes> pageSellerAttr =
        new PageImpl<>(Collections.singletonList(sellerAttributes));

    when(sellerAttributesRepo.findAllBySellerPid(sellerPid, pageable)).thenReturn(pageSellerAttr);
    Page<SellerAttributesDTO> result =
        sellerAttributesDTOService.getSellerAttribute(sellerPid, pageable);

    SellerAttributesDTO resultDto = result.getContent().get(0);
    assertEquals(resultDto.getVersion().intValue(), sellerAttributes.getVersion());
    assertEquals(resultDto.getSellerPid(), sellerAttributes.getSellerPid());
  }

  @Test
  void getSellerAttributesDTO_whenInvalidSeller_returnEmptyContent() {

    Pageable pageable = PageRequest.of(0, 10);

    Page<SellerAttributes> pageSellerAttr = new PageImpl<>(List.of());
    when(sellerAttributesRepo.findAllBySellerPid(sellerPid, pageable)).thenReturn(pageSellerAttr);

    Page<SellerAttributesDTO> result =
        sellerAttributesDTOService.getSellerAttribute(sellerPid, pageable);
    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void updateSellerAttributes_whenInvalidSeller_throwException() {

    SellerAttributesDTO dto = new SellerAttributesDTO();
    dto.setSellerPid(sellerPid);
    dto.setVersion(0);
    dto.setAdStrictApproval(false);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.empty());

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerAttributesDTOService.updateSellerAttribute(dto));

    assertEquals(ServerErrorCodes.SERVER_NON_EXISTENT_TARGET_PID, thrown.getErrorCode());
  }

  @Test
  void shouldUpdateSellerAttributesWhenHumanOptOutIsGiven() {

    // given
    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setAdStrictApproval(false);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setAdStrictApproval(false);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    // when
    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);

    // then
    assertEquals(requestDto.getHumanOptOut(), resultDto.getHumanOptOut());
  }

  @Test
  void updateSellerAttributes_whenSmartQPSEnabledTrueIsGivenWithPermissionRole_thenPass() {

    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setSmartQPSEnabled(true);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setAdStrictApproval(false);

    setupUserContext(User.Role.ROLE_ADMIN);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);
    assertEquals(requestDto.getSmartQPSEnabled(), resultDto.getSmartQPSEnabled());
  }

  @Test
  void updateSellerAttributes_whenSmartQPSEnabledFalseIsGivenWithPermissionRole_thenPass() {

    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setSmartQPSEnabled(false);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setSmartQPSEnabled(true);
    sellerAttributes.setAdStrictApproval(false);

    setupUserContext(User.Role.ROLE_ADMIN);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);
    assertEquals(requestDto.getSmartQPSEnabled(), resultDto.getSmartQPSEnabled());
  }

  @Test
  void updateSellerAttributes_whenSmartQPSEnabledChangedWithoutPermissionRole_thenThrowException() {

    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setSmartQPSEnabled(true);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setSmartQPSEnabled(false);

    setupUserContext(User.Role.ROLE_USER);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));

    var thrown =
        assertThrows(
            GenevaSecurityException.class,
            () -> sellerAttributesDTOService.updateSellerAttribute(requestDto));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, thrown.getErrorCode());
  }

  @Test
  void shouldUpdateSellerAttributesWhenSmartQPSEnabledFlagIsNull() {

    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setSmartQPSEnabled(true);
    sellerAttributes.setAdStrictApproval(false);

    setupUserContext(User.Role.ROLE_ADMIN);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);
    assertEquals(true, resultDto.getSmartQPSEnabled());
  }

  @Test
  void shouldUpdateHumanSamplingRatesWhenTheyAreNotNullInDto() {
    // given
    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setHumanPrebidSampleRate(10);
    requestDto.setHumanPostbidSampleRate(90);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setSmartQPSEnabled(true);
    sellerAttributes.setAdStrictApproval(false);
    sellerAttributes.setHumanPrebidSampleRate(20);
    sellerAttributes.setHumanPostbidSampleRate(20);

    setupUserContext(User.Role.ROLE_ADMIN);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    // when
    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);

    // then
    assertEquals(requestDto.getHumanPrebidSampleRate(), resultDto.getHumanPrebidSampleRate());
    assertEquals(requestDto.getHumanPostbidSampleRate(), resultDto.getHumanPostbidSampleRate());
  }

  @Test
  void shouldNotUpdateHumanSamplingRatesWhenTheyAreNullInDto() {
    // given
    var prebidSampleRate = 20;
    var postbidSampleRate = 30;
    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setSmartQPSEnabled(true);
    sellerAttributes.setAdStrictApproval(false);
    sellerAttributes.setHumanPrebidSampleRate(prebidSampleRate);
    sellerAttributes.setHumanPostbidSampleRate(postbidSampleRate);

    setupUserContext(User.Role.ROLE_ADMIN);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    // when
    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);

    // then
    assertEquals(prebidSampleRate, resultDto.getHumanPrebidSampleRate());
    assertEquals(postbidSampleRate, resultDto.getHumanPostbidSampleRate());
  }

  @Test
  void shouldUpdateSellerAttributesWhenCustomDealFloorEnabledFlagIsGiven() {
    // given
    SellerAttributesDTO requestDto = new SellerAttributesDTO();
    requestDto.setSellerPid(sellerPid);
    requestDto.setVersion(0);
    requestDto.setCustomDealFloorEnabled(true);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(sellerPid);
    sellerAttributes.setVersion(0);
    sellerAttributes.setCustomDealFloorEnabled(false);

    setupUserContext(User.Role.ROLE_ADMIN);

    when(sellerAttributesRepo.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(sellerAttributesRepo.save(any())).thenReturn(sellerAttributes);

    // when
    SellerAttributesDTO resultDto = sellerAttributesDTOService.updateSellerAttribute(requestDto);

    // then
    assertEquals(true, sellerAttributes.isCustomDealFloorEnabled());
  }
}
