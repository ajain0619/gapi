package com.nexage.app.util.validator;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RTBProfileValidator {
  private final CompanyRepository companyRepository;

  public void validateRtbProfile(RTBProfile rtbProfile) {
    if (rtbProfile == null) {
      return;
    }

    BigDecimal pubNetReserve = rtbProfile.getPubNetReserve();
    BigDecimal pubNetlowReserve = rtbProfile.getPubNetLowReserve();
    if ((pubNetReserve == null && pubNetlowReserve != null)
        || (pubNetReserve != null
            && pubNetlowReserve != null
            && pubNetReserve.compareTo(pubNetlowReserve) < 0)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_INVALID_LOW_RESERVE);
    }
  }

  public void validateUpdate(Long sellerPid, Optional<RTBProfile> rtbProfile) {

    if (!rtbProfile.isPresent()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND);
    }
    // Can't edit a profile that is not for this seller
    if (sellerPid.longValue()
        != rtbProfile.get().getDefaultRtbProfileOwnerCompanyPid().longValue()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }

    // Can’t edit a profile of company that has not default profiles enabled
    if (!companyRepository.isCompanyDefaultRTBProfilesEnabled(sellerPid)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY);
    }
  }
}
