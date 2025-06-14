package com.nexage.app.util.validator;

import com.nexage.admin.core.model.NativeVersion;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates the string representation of a native version.
 *
 * <p>There are two ways in which the string representation is valid:
 *
 * <ol>
 *   <li>it is {@code null}
 *   <li>passing it to {@link NativeVersion#fromActual} returns a non-{@code null} value
 * </ol>
 */
public class NativeVersionValidator extends BaseValidator<NativeVersionConstraint, String> {

  @Override
  public boolean isValid(String nativeVersion, ConstraintValidatorContext context) {
    return (nativeVersion == null) || (NativeVersion.fromActual(nativeVersion) != null);
  }
}
