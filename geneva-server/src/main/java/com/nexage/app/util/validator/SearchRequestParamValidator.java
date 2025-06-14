package com.nexage.app.util.validator;

import static java.util.Objects.isNull;

import com.google.common.primitives.Primitives;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchRequestParamValidator {

  public static <T> boolean isValid(Set<String> params, @NotNull Class<T> classType) {
    log.debug("params={} for classType={}", params, classType);
    boolean finalResult = true;
    if (!CollectionUtils.isEmpty(params) && !isNull(classType)) {
      Set<Field> candidates = getAllFields(new HashSet<>(), classType);
      Set<String> fieldNames =
          candidates.stream()
              .filter(
                  field ->
                      field.getType().isPrimitive()
                          || Primitives.isWrapperType(field.getType())
                          || field.getType().isEnum()
                          || field.getType().equals(String.class))
              .map(Field::getName)
              .collect(Collectors.toSet());
      for (String param : params) {
        if (!fieldNames.contains(param)) {
          finalResult = false;
          break;
        }
      }
    }
    return finalResult;
  }

  /** Validator contains validation logic for qf and qt lists */
  public static boolean isValid(List<String> qf, List<String> qt) {
    log.debug("fields={} - terms={}", qf, qt);
    // verify both lists match in size and no values in qt are null
    if (!CollectionUtils.isEmpty(qf) && !CollectionUtils.isEmpty(qt)) {
      // if sizes don't match, not valid
      if (qf.size() != qt.size()) {
        log.warn("Query field and term list sizes do not match.");
        return false;
      }
      // if a query term is null, not valid
      for (String term : qt) {
        if (StringUtils.isEmpty(term)) {
          log.warn("A query term was null.");
          return false;
        }
      }
    } else if ((CollectionUtils.isEmpty(qf) && !CollectionUtils.isEmpty(qt))
        || (!CollectionUtils.isEmpty(qf) && CollectionUtils.isEmpty(qt))) {
      // if one is null/empty and the other not null/empty, not valid
      log.warn("Either query field or term list was null.");
      return false;
    }
    return true;
  }

  private static Set<Field> getAllFields(Set<Field> fields, Class<?> type) {
    if (type.getSuperclass() != null) {
      getAllFields(fields, type.getSuperclass());
    }

    fields.addAll(Arrays.asList(type.getDeclaredFields()));

    return fields;
  }
}
