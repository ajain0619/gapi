package com.nexage.app.services.validation.sellingrule;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@Log4j2
@Component
public class AgeTargetValidator implements RuleTargetValidatorRegistry.Validator {

  private static final Set<String> knownAgeTargets =
      Stream.of("UNKNOWN_AND_UNDER_18", "18-20", "21-24", "25-34", "35-44", "45-54", "55-64", "65-")
          .collect(Collectors.toSet());

  @Override
  public void accept(RuleTargetDTO target) {

    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(target);
    Arrays.stream(target.getData().split(","))
        .forEach(
            ageGrp -> {
              if (!knownAgeTargets.contains(ageGrp)) {
                log.error(
                    "This age target does not exist. Received: {}, expected list: {}",
                    ageGrp,
                    knownAgeTargets);
                throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_AGEGRP);
              }
            });
  }

  @PostConstruct
  public void init() {
    log.info("Registering age target validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.AGE, this);
  }
}
