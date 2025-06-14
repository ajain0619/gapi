package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.repository.GeoSegmentRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.CustomObjectMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountryValidatorTest {

  @Spy private CustomObjectMapper objectMapper;
  @Mock private GeoSegmentRepository geoSegmentRepository;
  @InjectMocks private CountryValidator countryValidator;

  @Test
  void shouldThrowBadRequestWithInvalidJson() {
    RuleTargetDTO targetDTO = RuleTargetDTO.builder().data("invalid json").build();
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> countryValidator.accept(targetDTO));

    assertEquals(ServerErrorCodes.SERVER_TARGET_INVALID_COUNTRY_FORMAT, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenOneTargetNotExisting() {
    RuleTargetDTO targetDTO =
        RuleTargetDTO.builder()
            .data(
                "{\"geosegments\":[{\"woeid\":11,\"name\":\"United States Minor Outlying Islands"
                    + " (UM)\"},{\"woeid\":12,\"name\":\"United States (US)\"}]}")
            .build();

    given(
            geoSegmentRepository.existsCountryByWoeIdAndName(
                11L, "United States Minor Outlying Islands (UM)"))
        .willReturn(true);

    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> countryValidator.accept(targetDTO));

    assertEquals(ServerErrorCodes.SERVER_TARGET_INVALID_COUNTRY, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenAllTargetCountriesExist() {
    RuleTargetDTO targetDTO =
        RuleTargetDTO.builder()
            .data(
                "{\"geosegments\":[{\"woeid\":11,\"name\":\"United States Minor Outlying Islands"
                    + " (UM)\"},{\"woeid\":12,\"name\":\"United States (US)\"}]}")
            .build();

    given(
            geoSegmentRepository.existsCountryByWoeIdAndName(
                11L, "United States Minor Outlying Islands (UM)"))
        .willReturn(true);

    given(geoSegmentRepository.existsCountryByWoeIdAndName(12L, "United States (US)"))
        .willReturn(true);

    assertDoesNotThrow(() -> countryValidator.accept(targetDTO));
  }
}
