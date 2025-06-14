package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleTypeStringValidator {

  public static boolean isValid(String params) {
    log.debug("types={}", params);

    if (params == null || params.isBlank()) {
      return false;
    }

    List<String> paramList = Arrays.asList(params.split(","));
    long count =
        Stream.of(RuleType.values()).filter(type -> paramList.contains(type.name())).count();
    return count == paramList.size();
  }
}
