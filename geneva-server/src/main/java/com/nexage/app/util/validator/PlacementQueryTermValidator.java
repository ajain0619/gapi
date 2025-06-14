package com.nexage.app.util.validator;

import com.google.common.base.Strings;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlacementQueryTermValidator {

  private static final Set<String> VALID_SEARCH_FIELDS = Set.of("name", "memo");
  private static final Set<String> VALID_DUPLICATE_FIELDS = Set.of("name", "memo", "alias");

  public static boolean isValid(Map<String, String> queryTerms) {
    log.debug("queryTerms={}", queryTerms);

    if (Objects.isNull(queryTerms) || queryTerms.size() < 2) {
      return false;
    }

    String type = queryTerms.get("action");
    if (Strings.isNullOrEmpty(type) || !(type.equals("search") || type.equals("duplicate"))) {
      return false;
    }

    Set<String> validFields = type.equals("search") ? VALID_SEARCH_FIELDS : VALID_DUPLICATE_FIELDS;
    return queryTerms.keySet().stream()
        .noneMatch(key -> !validFields.contains(key) && !key.equals("action"));
  }
}
