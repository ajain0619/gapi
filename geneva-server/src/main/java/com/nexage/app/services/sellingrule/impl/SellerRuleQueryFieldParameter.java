package com.nexage.app.services.sellingrule.impl;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

/**
 * This is a representation of query field parameter (one of url query string parameter) specific to
 * seller rules. This is a fully pledged object ready to use in searching for seller rules based on
 * different criteria.
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SellerRuleQueryFieldParameter {

  public static final String NAME_FIELD_NAME = "name";
  public static final String TYPE_FIELD_NAME = "type";
  public static final String PID_FIELD_NAME = "pid";
  public static final String DF_PLACEMENT_FIELD_NAME = "deployedForPlacements";
  public static final String DF_SELLER_FIELD_NAME = "deployedForSeller";
  public static final String DF_SITE_FIELD_NAME = "deployedForSites";

  private final SearchQueryOperator operator;
  @Default private Optional<Set<RuleType>> types = Optional.empty();
  @Default private Optional<Set<Long>> rulePids = Optional.empty();
  @Default private Optional<String> ruleName = Optional.empty();
  @Default private Optional<Set<Long>> sitePids = Optional.empty();
  @Default private Optional<Set<Long>> placementPids = Optional.empty();
  @Default private Optional<Boolean> onlyRulesDeployedForSeller = Optional.empty();

  /**
   * This is a constructor method that creates an instance of query field parameter representation
   * specific to seller rules data fetching.
   *
   * @param queryParams a multi value map that comes from parsing an url query string parameter
   */
  public static <F extends QueryFieldKey, P extends MultiValueQueryParams>
      SellerRuleQueryFieldParameter createFrom(P queryParams, F[] queryFieldKeys) {
    SellerRuleQueryFieldParameterBuilder builder = SellerRuleQueryFieldParameter.builder();
    MultiValueMap<String, String> fields = queryParams.getFields();
    Stream.of(queryFieldKeys)
        .filter(f -> fields.containsKey(f.getName()))
        .forEach(field -> extractFieldValues(queryParams, field, builder));

    builder.operator = queryParams.getOperator();

    return builder.build();
  }

  /**
   * This method provides a functionality to set values for a given field in case this field has not
   * been present in <b>qf</b> request parameter and there is a need to actually force this field to
   * have a desired values anyway.
   *
   * @param field field for which values should be set
   * @param values values to be set for a given field
   * @param <F> type of the field
   */
  public <F extends QueryFieldKey> void whenAbsentForceFieldValues(F field, String... values) {
    List<String> valueList = Arrays.asList(values);
    // for all fields, in case value is already present, do nothing (leave the current value as is)
    switch (field.getName()) {
      case PID_FIELD_NAME:
        if (rulePids.isEmpty()) {
          this.rulePids = asMultiValue(fieldValues(field, valueList));
        }
        break;
      case NAME_FIELD_NAME:
        if (ruleName.isEmpty()) {
          ruleName = asSingleValue(fieldValues(field, valueList));
        }
        break;
      case TYPE_FIELD_NAME:
        if (types.isEmpty()) {
          Optional<Set<String>> stringTypes = asMultiValue(fieldValues(field, valueList));
          this.types = convertToRuleTypeEnumValues(stringTypes);
        }
        break;
      case DF_PLACEMENT_FIELD_NAME:
        if (placementPids.isEmpty()) {
          placementPids = asMultiValue(fieldValues(field, valueList));
        }
        break;
      case DF_SELLER_FIELD_NAME:
        if (onlyRulesDeployedForSeller.isEmpty()) {
          onlyRulesDeployedForSeller = asSingleValue(fieldValues(field, valueList));
        }
        break;
      case DF_SITE_FIELD_NAME:
        if (sitePids.isEmpty()) {
          sitePids = asMultiValue(fieldValues(field, valueList));
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown field ".concat(field.getName()));
    }
  }

  private static <F extends QueryFieldKey> void extractFieldValues(
      MultiValueQueryParams queryParametersMap,
      F field,
      SellerRuleQueryFieldParameterBuilder parameterBuilder) {

    switch (field.getName()) {
      case TYPE_FIELD_NAME:
        Optional<Set<String>> types = extractParamValues(queryParametersMap, field);
        types.ifPresentOrElse(
            x -> parameterBuilder.types(convertToRuleTypeEnumValues(types)),
            () -> parameterBuilder.types(Optional.empty()));
        break;
      case PID_FIELD_NAME:
        parameterBuilder.rulePids(extractParamValues(queryParametersMap, field));
        break;
      case NAME_FIELD_NAME:
        parameterBuilder.ruleName(extractParamValue(queryParametersMap, field));
        break;
      case DF_PLACEMENT_FIELD_NAME:
        parameterBuilder.placementPids(extractParamValues(queryParametersMap, field));
        break;
      case DF_SELLER_FIELD_NAME:
        parameterBuilder.onlyRulesDeployedForSeller(extractParamValue(queryParametersMap, field));
        break;
      case DF_SITE_FIELD_NAME:
        parameterBuilder.sitePids(extractParamValues(queryParametersMap, field));
        break;
      default:
        throw new IllegalArgumentException("Unknown field ".concat(field.getName()));
    }
  }

  private static Optional<Set<RuleType>> convertToRuleTypeEnumValues(Optional<Set<String>> types) {
    Stream<String> stringNames = types.stream().flatMap(Collection::stream);
    Set<RuleType> ruleTypes = stringNames.map(RuleType::valueOf).collect(Collectors.toSet());
    return Optional.of(ruleTypes);
  }

  /**
   * Extracts value from a field. This method is for obtaining value from a single field, so if such
   * field contains more then one value, the first one will be used.
   *
   * @param parameterMap map that contains {@code qf} parameter values from request query string
   * @param field a singgle-value field for which value should be obtained from a given map
   * @param <T> type of value that given field is declared to have
   * @return an {@link Optional} instance with value for a given field
   */
  private static <T, F extends QueryFieldKey> Optional<T> extractParamValue(
      MultiValueQueryParams parameterMap, F field) {
    T[] values = field.values(parameterMap.getFields());
    return asSingleValue(values);
  }

  /**
   * For each field we check if values are present (stream is not empty). In case stream is empty
   * return an Optional.empty() instance.<br>
   * Returning an empty stream is important since based on this fact values of a given field will
   * not be used while creating JPA specification (important while using SQL IN statements). See
   * {@link com.nexage.admin.core.specification.SellerRuleQueryFieldSpecification} for details.
   *
   * @param <T> type of value that given field is declared to have
   * @param parameterMap map that contains {@code qf} parameter values from request query string
   * @param field a multi-value field for which values should be obtained from a given map
   * @return an {@link Optional} instance with values for a given field
   */
  private static <T, F extends QueryFieldKey> Optional<Set<T>> extractParamValues(
      MultiValueQueryParams parameterMap, F field) {
    T[] fieldValues = field.values(parameterMap.getFields());
    return asMultiValue(fieldValues);
  }

  private static <T> Optional<T> asSingleValue(T[] values) {
    return Stream.of(values).findFirst();
  }

  private static <T> Optional<Set<T>> asMultiValue(T[] fieldValues) {
    Optional<Set<T>> values = Optional.empty();
    if (fieldValues.length != 0) {
      Set<T> typeSet = Set.of(fieldValues);
      values = Optional.of(typeSet);
    }
    return values;
  }

  private static <T> Optional<T> asSingleValue(Set<T> values) {
    return values.stream().findFirst();
  }

  private static <T> Optional<Set<T>> asMultiValue(Set<T> fieldValues) {
    Optional<Set<T>> values = Optional.empty();
    if (!fieldValues.isEmpty()) {
      values = Optional.of(fieldValues);
    }
    return values;
  }

  private <T, F extends QueryFieldKey> Set<T> fieldValues(F field, List<String> valueList) {
    Class<T> typeClass = field.typeClass();
    Object[] fieldValues = field.values(valueList);
    return Stream.of(fieldValues).map(typeClass::cast).collect(Collectors.toSet());
  }
}
