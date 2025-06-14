package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleTargetType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = RuleTargetValidator.class)
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuleTargetConstraint {
  String message() default "Rule Target has invalid value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  // temporary property before implementation extension to other target types
  // We can remove below element from annotation https://jira.vzbuilders.com/browse/MX-12971
  RuleTargetType[] allowedTargets() default {
    RuleTargetType.DEVICE_TYPE,
    RuleTargetType.CONTENT_CHANNEL,
    RuleTargetType.CONTENT_SERIES,
    RuleTargetType.CONTENT_RATING,
    RuleTargetType.CONTENT_GENRE,
    RuleTargetType.AD_FORMAT_TYPE,
    RuleTargetType.PLAYLIST_RENDERING_CAPABILITY,
    RuleTargetType.PUBLISHER,
    RuleTargetType.REVGROUP,
    RuleTargetType.DEAL_CATEGORY,
    RuleTargetType.CONTENT_LIVESTREAM,
    RuleTargetType.CONTENT_LANGUAGE
  };
}
