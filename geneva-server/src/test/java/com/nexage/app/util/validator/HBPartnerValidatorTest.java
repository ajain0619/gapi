package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.HbPartnerUtils;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HBPartnerValidatorTest {

  @Mock private HbPartnerUtils hbPartnerUtils;

  @Spy @InjectMocks HbPartnerValidator validator;

  @Test
  void testCreateHbPartnerWithVersion() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);

    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));

    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void testCreateHbPartnerWithPid() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setPid(1L);

    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));

    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void testCreateHbPartnerIncorrectFeePercent_greater_than_1() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setFeeType(FeeType.PERCENTAGE);
    hbPartnerDTOs.get(0).setFee(new BigDecimal(1.1));
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_INVALID_FEE, exception.getErrorCode());
  }

  @Test
  void testCreateHbPartnerIncorrectFeePercent_less_than_zero() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setFeeType(FeeType.PERCENTAGE);
    hbPartnerDTOs.get(0).setFee(new BigDecimal(-0.2));
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_INVALID_FEE, exception.getErrorCode());
  }

  @Test
  void testUpdateHbPartnerEntityNotFound() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    HbPartner hbPartner = null;

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void testUpdateHbPartnerVersionMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(1);
    assertThrows(
        StaleStateException.class, () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
  }

  @Test
  void testUpdateHbPartnerIDMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id_different");

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_ID_NOT_EDITABLE, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdateHbPartnerFormattedDefaultTypeEnabledMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(false);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");
    hbPartner.setFormattedDefaultTypeEnabled(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_FORMATTED_DEFAULT_TYPE_ENABLED_NOT_EDITABLE,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenHBPartnerNameMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(true);
    hbPartnerDTO.setPartnerHandler("invalid");
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");
    hbPartner.setFormattedDefaultTypeEnabled(true);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(false);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_HANDLER_NAME_NOT_KNOWN, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenUpdateHbPartnerFormattedDefaultTypeEnabledMatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(true);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");
    hbPartner.setFormattedDefaultTypeEnabled(true);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    assertDoesNotThrow(() -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
  }

  @Test
  void shouldThrowExceptionWhenFillMaxDurationEnabledWhenMultiImpressionBidDisabled() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    // when
    hbPartnerDTO.setMultiImpressionBid(false);
    hbPartnerDTO.setMaxAdsPerPod(null);
    hbPartnerDTO.setFillMaxDuration(true);
    // then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));
    assertEquals(
        ServerErrorCodes
            .SERVER_HB_PARTNER_CANNOT_ENABLE_FILL_MAX_DURATION_WHEN_MULTI_IMPRESSION_BID_IS_DISABLED,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenMaxAdsPerPodSetWhenMultiImpressionBidDisabled() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    // when
    hbPartnerDTO.setMultiImpressionBid(false);
    hbPartnerDTO.setMaxAdsPerPod(8);
    hbPartnerDTO.setFillMaxDuration(false);
    // then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));
    assertEquals(
        ServerErrorCodes
            .SERVER_HB_PARTNER_CANNOT_SET_MAX_ADS_PER_POD_WHEN_MULTI_IMPRESSION_BID_IS_DISABLED,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenBothMaxAdsPerPodAndFillMaxDurationAreSetTogether() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    // when
    hbPartnerDTO.setMultiImpressionBid(true);
    hbPartnerDTO.setMaxAdsPerPod(8);
    hbPartnerDTO.setFillMaxDuration(true);
    // then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validator.isValidForCreate(hbPartnerDTO));
    assertEquals(
        ServerErrorCodes
            .SERVER_HB_PARTNER_CANNOT_SET_BOTH_MAX_ADS_PER_POD_AND_FILL_MAX_DURATION_TOGETHER,
        exception.getErrorCode());
  }

  @Test
  void
      shouldNotThrowExceptionWhenMaxAdsPerPodSetWithMultiImpressionBidEnabledAndFillMaxDurationDisabled() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setPid(null);
    hbPartnerDTOs.get(0).setVersion(null);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    // when
    hbPartnerDTO.setMultiImpressionBid(true);
    hbPartnerDTO.setMaxAdsPerPod(8);
    hbPartnerDTO.setFillMaxDuration(false);
    // then
    assertDoesNotThrow(() -> validator.isValidForCreate(hbPartnerDTO));
  }

  @Test
  void
      shouldNotThrowExceptionWhenFillMaxDurationSetWithMultiImpressionBidEnabledAndMaxAdsPerPodDisabled() {
    List<HbPartnerDTO> hbPartnerDTOs = TestObjectsFactory.gimme(1, HbPartnerDTO.class);
    hbPartnerDTOs.get(0).setPid(null);
    hbPartnerDTOs.get(0).setVersion(null);
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);
    HbPartnerDTO hbPartnerDTO = hbPartnerDTOs.get(0);
    // when
    hbPartnerDTO.setMultiImpressionBid(true);
    hbPartnerDTO.setFillMaxDuration(true);
    hbPartnerDTO.setMaxAdsPerPod(null);
    // then
    assertDoesNotThrow(() -> validator.isValidForCreate(hbPartnerDTO));
  }

  @Test
  void shouldThrowExceptionWhenUpdateHbPartnerMultiImpressionBidMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setFormattedDefaultTypeEnabled(false);
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");

    // when
    hbPartnerDTO.setMultiImpressionBid(false);
    hbPartner.setMultiImpressionBid(true);

    // then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_MULTI_IMPRESSION_BID_NOT_EDITABLE,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdateHbPartnerFillMaxDurationMismatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setFormattedDefaultTypeEnabled(false);
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");

    // when
    hbPartnerDTO.setFillMaxDuration(true);
    hbPartner.setFillMaxDuration(false);

    // then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_FILL_MAX_DURATION_NOT_EDITABLE,
        exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenUpdateHbPartnerMultiImpressionBidMatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(false);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);

    // when
    hbPartnerDTO.setMultiImpressionBid(true);
    hbPartner.setMultiImpressionBid(true);

    // then
    assertDoesNotThrow(() -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
  }

  @Test
  void shouldNotThrowExceptionWhenUpdateHbPartnerFillMaxDurationMatch() {
    HbPartnerDTO hbPartnerDTO = TestObjectsFactory.createNewHbPartnerDTO();
    hbPartnerDTO.setVersion(2);
    hbPartnerDTO.setId("partner_id");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(false);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setVersion(2);
    hbPartner.setId("partner_id");
    when(hbPartnerUtils.isValidPartnerName(any())).thenReturn(true);

    // when
    hbPartnerDTO.setFillMaxDuration(false);
    hbPartner.setFillMaxDuration(false);

    // then
    assertDoesNotThrow(() -> validator.isValidForUpdate(hbPartnerDTO, hbPartner));
  }
}
