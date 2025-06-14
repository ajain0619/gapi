package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.HbPartnerUtils;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
// Note : A ticket has been created (https://jira.vzbuilders.com/browse/MX-13310) to make use of
// ConstraintValidator
// instead of the implementation here. It will require a framework/utility method `UpdateGroup`
// similar to `CreateGroup`
// when that is ready, the following should be moved to that mechanism of validating the
// `HbPartnerDTO`
@Component
public class HbPartnerValidator {

  private final HbPartnerUtils hbPartnerUtils;

  public void isValidForCreate(HbPartnerDTO dto) {
    validateHbPartnerAttributes(dto);
    if (dto.getPid() != null || dto.getVersion() != null) {
      log.error("Error creating HB Partner, unwanted fields in request");
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  public void isValidForUpdate(HbPartnerDTO dto, HbPartner entity) {
    if (entity == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
    }
    if (!entity.getVersion().equals(dto.getVersion())) {
      throw new StaleStateException("Hb Partner has a different version of data");
    }
    if (!entity.getId().equals(dto.getId())) {
      log.error("HB Partner ID is not editable");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_ID_NOT_EDITABLE);
    }
    if (entity.isFormattedDefaultTypeEnabled() != (dto.isFormattedDefaultTypeEnabled())) {
      log.error("HB Partner formattedDefaultTypeEnabled is not editable");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_HB_PARTNER_FORMATTED_DEFAULT_TYPE_ENABLED_NOT_EDITABLE);
    }
    if (entity.isMultiImpressionBid() != (dto.isMultiImpressionBid())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_HB_PARTNER_MULTI_IMPRESSION_BID_NOT_EDITABLE);
    }
    if (entity.isFillMaxDuration() != (dto.isFillMaxDuration())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_HB_PARTNER_FILL_MAX_DURATION_NOT_EDITABLE);
    }
    validateHbPartnerAttributes(dto);
  }

  private void validateHbPartnerAttributes(HbPartnerDTO dto) {
    if (FeeType.PERCENTAGE == dto.getFeeType() && !isFeeBetweenZeroAndOne(dto.getFee())) {
      log.error("Fee should be between 0 and 1 for FeeType = Percentage/Pub Rev");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_INVALID_FEE);
    }

    if (!hbPartnerUtils.isValidPartnerName(dto.getPartnerHandler())) {
      log.error("Invalid/Unknown partner Handler name");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_HB_PARTNER_HANDLER_NAME_NOT_KNOWN);
    }

    validateHbPartnerMultiBiddingAttributes(dto);
  }

  private void validateHbPartnerMultiBiddingAttributes(HbPartnerDTO dto) {
    if (dto.isFillMaxDuration() && (!dto.isMultiImpressionBid())) {
      throw new GenevaValidationException(
          ServerErrorCodes
              .SERVER_HB_PARTNER_CANNOT_ENABLE_FILL_MAX_DURATION_WHEN_MULTI_IMPRESSION_BID_IS_DISABLED);
    }

    if ((dto.getMaxAdsPerPod() != null) && (!dto.isMultiImpressionBid())) {
      throw new GenevaValidationException(
          ServerErrorCodes
              .SERVER_HB_PARTNER_CANNOT_SET_MAX_ADS_PER_POD_WHEN_MULTI_IMPRESSION_BID_IS_DISABLED);
    }

    if (dto.isFillMaxDuration() && (dto.getMaxAdsPerPod() != null)) {
      throw new GenevaValidationException(
          ServerErrorCodes
              .SERVER_HB_PARTNER_CANNOT_SET_BOTH_MAX_ADS_PER_POD_AND_FILL_MAX_DURATION_TOGETHER);
    }
  }

  private static boolean isFeeBetweenZeroAndOne(BigDecimal fee) {
    return fee.compareTo(BigDecimal.ZERO) >= 0 && fee.compareTo(BigDecimal.ONE) <= 0;
  }
}
