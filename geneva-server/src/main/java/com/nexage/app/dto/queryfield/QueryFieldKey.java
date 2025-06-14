package com.nexage.app.dto.queryfield;

import com.nexage.app.util.validator.rule.queryfield.AbstractQueryFieldParameterValidator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

/**
 * This interface describes a particular field (actually only its key, not a value) that is used in
 * query field searching. Query field searching is based on criteria specified in <b>qf</b> request
 * parameter. All classes that define such fields are supposed to implement this interface to
 * deliver the same set of functionality. For example, custom instances of {@link
 * AbstractQueryFieldParameterValidator} could be used to validate such endpoint specific query
 * field parameter values.
 */
public interface QueryFieldKey {

  /**
   * A getter method for a name of this query field
   *
   * @return name of the field
   */
  String getName();

  /**
   * A getter method for a type of this query field value
   *
   * @return type of this query field value(s)
   */
  QueryFieldValueType getType();

  /**
   * A getter method for values that are allowed for this field. No other values (other then
   * defined) can be used. Otherwise, validation done by {@link
   * AbstractQueryFieldParameterValidator} will fail.
   *
   * @return an array of allowed values
   */
  Object[] getAllowedValues();

  /**
   * A getter method for default values defined for this field. In case this field does not have any
   * values specified in request parameter, but the field itself is present in request query string,
   * those values will be set for this field.
   *
   * @return an array of default values. It is an array (not a single {@link Object} instance, since
   *     some field can be multi-value fields.
   */
  Object[] getDefaultValues();

  /**
   * A method used to check if a given value is one of the values that are allowed only for this
   * field
   *
   * @param value value to check
   * @return true if a given value is an allowed only value
   */
  default boolean hasAllowedValue(String value) {
    return Stream.of(getAllowedValues()).anyMatch(x -> Objects.equals(x, getType().convert(value)));
  }

  /**
   * Method to check weather a given value is a valid one in terms of its defined type
   *
   * @param valueToCheck value to check its validity against a its declared type
   * @return true if the value is valid, false otherwise
   */
  default boolean isValid(String valueToCheck) {
    return getType().valid(valueToCheck);
  }

  /**
   * Method to check weather this query field has its allowed values pre-defined
   *
   * @return true if this field has allowed values defined, false otherwise
   */
  default boolean hasAllowedValuesDefined() {
    return Objects.requireNonNull(getAllowedValues()).length > 0;
  }

  /**
   * Method used to obtain a class of this field value type. <br>
   * Unfortunately enum type can not be made generic so here we want to suppress {@code unchecked}
   * cast warning since we are pretty sure that this should work. Otherwise, tests will fail (see
   * SellerRuleQueryFieldTest class).
   *
   * @param <T> type of the field
   * @return a class instance for this field value type
   */
  @SuppressWarnings("unchecked")
  default <T> Class<T> typeClass() {
    return (Class<T>) getType().typeClass();
  }

  /**
   * This method returns values for a particular field in their defined types (i.e. String or Long)
   * by obtaining then from a given value map.
   *
   * @param fieldMap map that contains field and their values obtained from a request query string
   *     parameter. In case values for particular field are not present, a default values for that
   *     field are set.
   * @param <T> type of values being returned
   * @return an array of values for this particular field converted to its declared type
   */
  default <T> T[] values(MultiValueMap<String, String> fieldMap) {
    List<String> stringValues = fieldMap.getOrDefault(getName(), List.of());
    return values(stringValues);
  }

  /**
   * Method that converts a given list of {@link String} values to an array of items of the same
   * type as type of this field key.
   *
   * @param values values to convert
   * @param <T> type the values will be converted to
   * @return an array of converted {@link String} values
   */
  @SuppressWarnings("unchecked")
  default <T> T[] values(List<String> values) {
    Class<T> typeClass = typeClass();
    List<String> valuesWithoutBlanks =
        values.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    if (valuesWithoutBlanks.isEmpty()) {
      return (T[])
          Stream.of(getDefaultValues())
              .filter(typeClass::isInstance)
              .map(typeClass::cast)
              .toArray();
    }

    return (T[])
        values.stream()
            .map(getType()::convert)
            .filter(typeClass::isInstance)
            .map(typeClass::cast)
            .toArray();
  }
}
