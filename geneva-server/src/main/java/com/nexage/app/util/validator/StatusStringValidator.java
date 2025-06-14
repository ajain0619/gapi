package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.Status;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusStringValidator {

  public static boolean isValid(String params) {
    log.debug("types={}", params);

    if (params == null || params.isBlank()) {
      return false;
    }

    List<String> paramList = Arrays.asList(params.split(","));
    // searching for entities with Status.DELETED is not permitted, so it will fail validation
    long count =
        Stream.of(Status.ACTIVE, Status.INACTIVE)
            .filter(status -> paramList.contains(status.name()))
            .count();
    return count == paramList.size();
  }
}
