package com.nexage.app.services.validation.sellingrule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.GeoSegment;
import com.nexage.admin.core.repository.GeoSegmentRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@RequiredArgsConstructor
@Component
@Log4j2
public class CountryValidator implements RuleTargetValidatorRegistry.Validator {

  private final ObjectMapper objectMapper;
  private final GeoSegmentRepository geoSegmentRepository;

  @PostConstruct
  public void init() {
    log.info("Registering country validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.COUNTRY, this);
  }

  @Override
  public void accept(RuleTargetDTO ruleTargetDTO) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(ruleTargetDTO);

    final Countries countries;
    try {
      countries = objectMapper.readValue(ruleTargetDTO.getData(), Countries.class);
    } catch (JsonProcessingException e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_COUNTRY_FORMAT);
    }
    if (countries.any(this::notInRepository)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_COUNTRY);
    }
  }

  private boolean notInRepository(GeoSegment segment) {
    return !geoSegmentRepository.existsCountryByWoeIdAndName(segment.getWoeid(), segment.getName());
  }

  private static class Countries {
    List<GeoSegment> geosegments;

    public boolean any(Predicate<? super GeoSegment> predicate) {
      return geosegments.stream().anyMatch(predicate);
    }
  }
}
