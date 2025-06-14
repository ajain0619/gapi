package com.nexage.app.util.validator.rule.queryfield;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationMessages;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

/**
 * Validator that is responsible for validation of types that represent query fields. A {@link
 * Class} or {@link Enum} types are usually used to represent those query fields. This validator
 * checks whether values of particular key (including defaults, if any) are of correct type and
 * their values are allowed (if applicable).
 *
 * @param <F> type of field that validator will perform validation against
 * @param <C> type of constraint validator
 * @param <P> type of custom {@link MultiValueQueryParams} implementation where a given validation
 *     constraint is used
 */
@Log4j2
public abstract class AbstractQueryFieldParameterValidator<
        F extends QueryFieldKey, C extends Annotation, P extends MultiValueQueryParams>
    extends BaseValidator<C, P> {

  private final F[] queryFieldKeys;

  protected AbstractQueryFieldParameterValidator(F[] queryFieldKeys) {
    this.queryFieldKeys = queryFieldKeys;
  }

  /**
   * This method is analysing a given map and checks if values that were passed in fulfils all the
   * conditions set for particular seller rule query field.
   *
   * @param queryFieldMap map which stores keys (fields) and values obtained and parsed from a
   *     request parameter
   * @param validatorContext a validator context object
   * @return validation result that stores info about validation performed (its status and custom
   *     message)
   */
  protected boolean validate(
      MultiValueMap<String, String> queryFieldMap, ConstraintValidatorContext validatorContext) {
    log.debug("validating queryFieldMap={}", queryFieldMap);

    if (queryFieldMap == null) {
      buildConstraintViolationWithTemplate(validatorContext, ValidationMessages.WRONG_IS_EMPTY);
      return false;
    }

    // get only known fields
    Set<F> knownFields =
        Stream.of(queryFieldKeys)
            .filter(field -> queryFieldMap.containsKey(field.getName()))
            .collect(Collectors.toSet());

    // we treat missing or empty qf field as a valid input
    if (knownFields.isEmpty()) {
      return true;
    }

    // validate only fields that are specified in queryField
    Stream<F> validatedFields =
        knownFields.stream()
            .filter(
                field -> {
                  List<String> fieldValues = getFieldValuesApplyingDefaults(queryFieldMap, field);
                  return validateField(fieldValues, field, validatorContext);
                });

    boolean allFieldsAreOk = validatedFields.count() == knownFields.size();
    if (!allFieldsAreOk) {
      buildConstraintViolationWithTemplate(
          validatorContext, ValidationMessages.QUERY_FIELD_VALIDATION_FAILED);
    }
    return allFieldsAreOk;
  }

  private List<String> getFieldValuesApplyingDefaults(
      MultiValueMap<String, String> queryFieldMap, F field) {
    // apply default field values in case they are missing in the input map
    List<String> defaultValues =
        Stream.of(field.getDefaultValues()).map(Object::toString).collect(Collectors.toList());
    List<String> fieldValues = queryFieldMap.get(field.getName());
    List<String> valuesWithoutBlanks =
        fieldValues.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    if (valuesWithoutBlanks.isEmpty()) {
      fieldValues = defaultValues;
    }
    return fieldValues;
  }

  private boolean validateField(
      List<String> queryFieldValues, F field, ConstraintValidatorContext validatorContext) {
    // check each value against their type
    boolean finalResult = checkTypeCorrectness(field, queryFieldValues, validatorContext);

    // also check against valid values (if applicable)
    finalResult &= checkAllowedValues(field, queryFieldValues, validatorContext);

    return finalResult;
  }

  private boolean checkAllowedValues(
      F field, List<String> values, ConstraintValidatorContext validatorContext) {
    if (!field.hasAllowedValuesDefined()) {
      return true;
    }
    Stream<String> stream = values.stream().filter(v -> !field.hasAllowedValue(v));
    boolean valuesAreOk = stream.count() == 0;
    if (!valuesAreOk) {
      buildConstraintViolationWithTemplate(
          validatorContext,
          String.format(
              ValidationMessages.QUERY_FIELD_FAILED_DUE_TO,
              field.getName(),
              "One of the field value is not allowed"));
    }
    return valuesAreOk;
  }

  private boolean checkTypeCorrectness(
      F field, List<String> values, ConstraintValidatorContext validatorContext) {
    Stream<String> stream = values.stream().filter(v -> !field.isValid(v));
    boolean valuesAreOk = stream.count() == 0;
    if (!valuesAreOk) {
      buildConstraintViolationWithTemplate(
          validatorContext,
          String.format(
              ValidationMessages.QUERY_FIELD_FAILED_DUE_TO,
              field.getName(),
              "One of the value for this field has invalid type"));
    }
    return valuesAreOk;
  }

  /**
   * Extract all values for given key and wrap as optional object of type set
   *
   * @param parameterMap
   * @param field
   * @return optional set of values
   */
  private <T, V extends QueryFieldKey> Optional<Set<T>> extractParamValues(
      MultiValueQueryParams parameterMap, V field) {
    T[] fieldValues = field.values(parameterMap.getFields());
    return asMultiValue(fieldValues);
  }

  /**
   * wrap given array in optional object
   *
   * @param fieldValues
   * @return optional object wrap array of filedValues if present or Empty optional
   */
  private <T> Optional<Set<T>> asMultiValue(T[] fieldValues) {
    Optional<Set<T>> values = Optional.empty();
    if (fieldValues.length != 0) {
      Set<T> typeSet = Set.of(fieldValues);
      values = Optional.of(typeSet);
    }
    return values;
  }

  /**
   * for given key check if values are not null
   *
   * @param queryParams
   * @return true/false , comparing queryParams for optional empty object
   */
  public <T extends QueryFieldKey> boolean validateAllFieldsHaveValues(
      MultiValueQueryParams queryParams, T[] fields) {

    return Stream.of(fields)
        .filter(field -> queryParams.getFields().containsKey(field.getName()))
        .noneMatch(field -> extractParamValues(queryParams, field).equals(Optional.empty()));
  }
}
