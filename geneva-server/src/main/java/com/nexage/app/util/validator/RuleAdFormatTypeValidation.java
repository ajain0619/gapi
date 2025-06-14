package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.AdFormatType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RuleAdFormatTypeValidation implements RuleTargetValidation {

  @Override
  public boolean isValid(String data) {
    return EnumUtils.isValidEnum(AdFormatType.class, data);
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.AD_FORMAT_TYPE;
  }
}
