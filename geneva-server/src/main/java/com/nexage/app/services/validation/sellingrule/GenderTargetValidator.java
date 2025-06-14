package com.nexage.app.services.validation.sellingrule;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@Log4j2
@Component
public class GenderTargetValidator implements RuleTargetValidatorRegistry.Validator {

  private static final Set<String> VALID_GENDERS = Sets.newHashSet("M", "F");

  @Override
  public void accept(RuleTargetDTO ruleTargetDto) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(ruleTargetDto);
    if (!VALID_GENDERS.contains(ruleTargetDto.getData())) {
      log.error("Unknown/unsupported/invalid gender: {}", ruleTargetDto.getData());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER);
    }
  }

  @PostConstruct
  public void init() {
    log.info("Registering gender target validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.GENDER, this);
  }
}
