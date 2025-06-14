package com.ssp.geneva.common.settings.service;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import java.util.List;

public interface GlobalConfigService {

  /**
   * Retrieves a global config by property and return its value as a {@code String}. When the config
   * is not found, {@code null} is returned.
   *
   * @param property a property by which the global config is retrieved.
   * @return the config's value or {@code null} if the config is not found.
   */
  String getStringValue(GlobalConfigProperty property);

  /**
   * Retrieves a global config by property and return its value as a {@code Integer}.
   *
   * @param property a property by which the global config is retrieved.
   * @return the config's value or {@code null} if the config is not found or it cannot be parsed as
   *     {@code Integer}.
   */
  Integer getIntegerValue(GlobalConfigProperty property);

  /**
   * Retrieves a global config by property and return its value as a {@code Boolean}. This method
   * will return {@code true} if the value is a string equal to {@code "true"} ignoring case.
   * Otherwise, {@code false} is returned.
   *
   * @param property a property by which the global config is retrieved.
   * @return the config's value or {@code null} if the config is not found.
   */
  Boolean getBooleanValue(GlobalConfigProperty property);

  /**
   * Retrieves a global config by property and return its value as a list of {@code Long}s.
   *
   * @param property a property by which the global config is retrieved.
   * @return the config's value or an empty list if the config is not found or it cannot be parsed
   *     as a list of numbers.
   */
  List<Long> getLongListValue(GlobalConfigProperty property);
}
