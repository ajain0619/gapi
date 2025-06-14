package com.nexage.admin.core.util;

import com.google.common.base.Splitter;
import com.nexage.admin.core.model.GlobalConfig;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GlobalConfigUtil {

  private static final String NFE_ERROR_MESSAGE = "NumberFormatException in reading property {}";

  private GlobalConfigUtil() {}

  /**
   * Decodes a given configuration value as a {@code Long}. If there is a {@code
   * NumberFormatException} on parsing the value, {@code null} is returned.
   *
   * @param config a configuration whose value is to be decoded.
   * @return the decoded {@code Long} value.
   */
  public static Long getLongValue(GlobalConfig config) {
    try {
      return Long.valueOf(config.getValue());
    } catch (NumberFormatException nfe) {
      log.error(NFE_ERROR_MESSAGE, config.getProperty(), nfe);
    }
    return null;
  }

  /**
   * Decodes a given configuration value as an {@code Integer}. If there is a {@code
   * NumberFormatException} on parsing the value, {@code null} is returned.
   *
   * @param config a configuration whose value is to be decoded.
   * @return the decoded {@code Integer} value.
   */
  public static Integer getIntegerValue(GlobalConfig config) {
    try {
      return Integer.valueOf(config.getValue());
    } catch (NumberFormatException nfe) {
      log.error(NFE_ERROR_MESSAGE, config.getProperty(), nfe);
    }
    return null;
  }

  /**
   * Decodes a given configuration value as a {@code Boolean}. This method will return {@code true}
   * if the value is a string equal to {@code "true"} ignoring case. Otherwise, {@code false} is
   * returned.
   *
   * @param config a configuration whose value is to be decoded.
   * @return the decoded {@code Boolean} value.
   */
  public static Boolean getBooleanValue(GlobalConfig config) {
    return Boolean.valueOf(config.getValue());
  }

  /**
   * Decodes a given configuration value as a comma separated list of numbers ({@code Long}s). Note:
   * This method is completely safe to call. It will never throw an exception. It will return an
   * empty list if there is a failure.
   *
   * @param config a configuration whose value is to be decoded.
   * @return the decoded list of {@code Long}s.
   */
  public static List<Long> getCsvValueAsLongList(GlobalConfig config) {
    try {
      return Splitter.on(",")
          .omitEmptyStrings()
          .trimResults()
          .splitToList(config.getValue())
          .stream()
          .map(Long::valueOf)
          .collect(Collectors.toList());
    } catch (NumberFormatException nfe) {
      log.error(NFE_ERROR_MESSAGE, config, nfe);
    } catch (Exception e) {
      log.error("Exception in reading property {}", config, e);
    }
    return List.of();
  }
}
