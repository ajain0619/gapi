package com.nexage.app.services.validation.sellingrule;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class RuleTargetValidatorRegistry {
  private static final Pattern CSV_NUMBER = Pattern.compile("\\d+(,\\d+)*");

  public interface Validator extends Consumer<RuleTargetDTO> {}

  static final Validator DEFAULT_NOT_BLANK_VALIDATOR =
      target -> {
        if (StringUtils.isBlank(target.getData())) {
          throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK);
        }
      };
  public static final Validator COMMA_SEPARATED_NUMBERS_VALIDATOR =
      target -> {
        DEFAULT_NOT_BLANK_VALIDATOR.accept(target);
        if (!CSV_NUMBER.matcher(target.getData()).matches()) {
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_NOT_COMMA_SEPARATED_NUMBERS);
        }
      };

  private static final EnumMap<RuleTargetType, Validator> VALIDATORS =
      new EnumMap<>(RuleTargetType.class);

  static void registerValidator(RuleTargetType targetType, Validator validator) {
    VALIDATORS.put(targetType, validator);
  }

  static void clearValidators() {
    VALIDATORS.clear();
  }

  public static void validate(RuleTargetDTO target) {
    VALIDATORS.getOrDefault(target.getTargetType(), DEFAULT_NOT_BLANK_VALIDATOR).accept(target);
  }
}
