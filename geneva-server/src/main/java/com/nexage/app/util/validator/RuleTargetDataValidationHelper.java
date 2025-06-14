package com.nexage.app.util.validator;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class RuleTargetDataValidationHelper {

  private RuleTargetDataValidationHelper() {}

  public static List<Long> convertToList(String data, String delimiter) {
    if (StringUtils.isBlank(data)) {
      return emptyList();
    }

    try {
      return Arrays.stream(data.split(delimiter))
          .map(value -> Long.parseLong(value.trim()))
          .collect(Collectors.toList());
    } catch (NumberFormatException e) {
      return emptyList();
    }
  }

  public static boolean hasUniqueElements(List<Long> list) {
    Set<Long> set = new HashSet<>(list);
    return list.size() == set.size();
  }
}
