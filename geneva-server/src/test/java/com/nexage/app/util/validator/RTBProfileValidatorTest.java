package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RTBProfileValidatorTest {
  @Mock private CompanyRepository companyRepository;

  @InjectMocks RTBProfileValidator rtbProfileValidator;

  @Test
  void testInvalidRtbProfile() {
    RTBProfile rtbProfile = new RTBProfile();
    BigDecimal pubNetLowReserve = new BigDecimal(2);
    rtbProfile.setPubNetLowReserve(pubNetLowReserve);
    assertThrows(
        GenevaValidationException.class, () -> rtbProfileValidator.validateRtbProfile(rtbProfile));
  }

  @Test
  void updateRTBProfilesDefaultProfilesNotEnabled() {

    Optional<RTBProfile> rtbProfile = Optional.of(new RTBProfile());
    rtbProfile.get().setDefaultRtbProfileOwnerCompanyPid(1L);

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileValidator.validateUpdate(1L, rtbProfile));
    assertEquals(
        ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY,
        exception.getErrorCode());
  }

  @Test
  void updateRTBProfilesTestInvalidSeller() {

    Optional<RTBProfile> rtbProfile = Optional.of(new RTBProfile());
    rtbProfile.get().setDefaultRtbProfileOwnerCompanyPid(2L);

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileValidator.validateUpdate(1L, rtbProfile));
    assertEquals(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void updateRTBProfilesTestInvalidProfile() {

    Optional<RTBProfile> rtbProfile = Optional.empty();

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileValidator.validateUpdate(1L, rtbProfile));
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND, exception.getErrorCode());
  }
}
