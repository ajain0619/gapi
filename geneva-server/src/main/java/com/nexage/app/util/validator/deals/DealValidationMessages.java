package com.nexage.app.util.validator.deals;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Contains validation messages for deals validators */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DealValidationMessages {
  public static final String DEAL_CURRENCY_NOT_SUPPORTED = "Currency is not supported";
  public static final String DEAL_CURRENCY_SHOULD_BE_DEFAULT = "Deal currency should be set to %s";
  public static final String DEAL_CURRENCY_SHOULD_BE_DEFAULT_OR_COMMON =
      "Deal currency should be set to %s or common for all publishers";
}
