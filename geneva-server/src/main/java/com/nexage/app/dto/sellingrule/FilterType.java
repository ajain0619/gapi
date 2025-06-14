package com.nexage.app.dto.sellingrule;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Specifies the allowed filter types. */
public enum FilterType {
  /** The block filter type. */
  BLOCKLIST("1");

  private final String value;

  FilterType(String value) {
    this.value = value;
  }

  private static final Map<String, FilterType> VALUE_TO_FILTER_TYPE =
      Stream.of(values())
          .collect(Collectors.toMap(filterType -> filterType.value, Function.identity()));

  private static final Set<String> NAMES =
      Stream.of(values()).map(FilterType::toString).collect(Collectors.toSet());

  /**
   * Check if there is a filter type with the given name.
   *
   * @param enumName the name to check for
   * @return {@code boolean} indicating if there is a filter type with the given name
   */
  public static boolean enumValueWithNameExists(String enumName) {
    return NAMES.contains(enumName);
  }

  /**
   * Convert a filter type name representation to its value representation.
   *
   * @param enumName the name representation to convert
   * @return the value representation or {@code null} if there is no filter type with that name
   */
  public static String convertEnumNameToValue(String enumName) {
    return enumValueWithNameExists(enumName) ? FilterType.valueOf(enumName).value : null;
  }

  /**
   * Convert a filter type value representation to its name representation.
   *
   * @param value the value representation to convert
   * @return the name representation or {@code null} if there is no filter type with that value
   */
  public static String convertValueToEnumName(String value) {
    var filterType = VALUE_TO_FILTER_TYPE.get(value);

    return (filterType == null) ? null : filterType.name();
  }
}
