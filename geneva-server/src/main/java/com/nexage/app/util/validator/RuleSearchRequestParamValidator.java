package com.nexage.app.util.validator;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleSearchRequestParamValidator {

  public static boolean isValid(Set<String> searchFields, String searchTerm) {
    log.debug("searchFields={} for searchTerm={}", searchFields, searchTerm);

    if (searchFields == null || searchTerm == null) {
      return true;
    }

    // if looking for only pid, make sure the term is numeric and non negative
    if (searchFields.contains("pid")
        && !searchFields.contains("name")
        && !searchTerm.chars().allMatch(Character::isDigit)) {
      return false;
    }

    // only pid and name allowed
    return searchFields.stream().noneMatch(field -> !field.equals("pid") && !field.equals("name"));
  }
}
