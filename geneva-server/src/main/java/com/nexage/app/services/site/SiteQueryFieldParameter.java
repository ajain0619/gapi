package com.nexage.app.services.site;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

/**
 * This is a representation of query field parameter (one of url query string parameter) specific to
 * site. This is a fully pledged object ready to use in searching for sites based on different
 * criteria.
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SiteQueryFieldParameter {

  public static final String PID_FIELD_NAME = "pid";
  public static final String NAME_FIELD_NAME = "name";
  public static final String GLOBAL_ALIAS_NAME_FIELD_NAME = "globalAliasName";
  public static final String COMPANY_PID_FIELD_NAME = "companyPid";
  public static final String STATUS_FIELD_NAME = "status";
  public static final String COMPANY_NAME_FIELD_NAME = "companyName";

  private final SearchQueryOperator operator;
  @Builder.Default private Optional<Set<Long>> pids = Optional.empty();
  @Builder.Default private Optional<Set<Long>> companyPids = Optional.empty();
  @Builder.Default private Optional<String> companyName = Optional.empty();
  @Builder.Default private Optional<String> name = Optional.empty();
  @Builder.Default private Optional<String> globalAliasName = Optional.empty();
  @Builder.Default private Optional<Set<Integer>> status = Optional.empty();

  /**
   * This is a constructor method that creates an instance of query field parameter representation
   * specific to sites data fetching.
   *
   * @param queryParams a multi value map that comes from parsing an url query string parameter
   */
  public static <F extends QueryFieldKey, P extends MultiValueQueryParams>
      SiteQueryFieldParameter createFrom(P queryParams, F[] queryFieldKeys) {
    SiteQueryFieldParameter.SiteQueryFieldParameterBuilder builder =
        SiteQueryFieldParameter.builder();
    MultiValueMap<String, String> fields = queryParams.getFields();
    Stream.of(queryFieldKeys)
        .filter(f -> fields.containsKey(f.getName()))
        .forEach(field -> extractFieldValues(queryParams, field, builder));

    builder.operator = queryParams.getOperator();

    return builder.build();
  }

  private static void extractFieldValues(
      MultiValueQueryParams queryParametersMap,
      QueryFieldKey field,
      SiteQueryFieldParameter.SiteQueryFieldParameterBuilder parameterBuilder) {

    switch (field.getName()) {
      case PID_FIELD_NAME:
        parameterBuilder.pids(extractParamValues(queryParametersMap, field));
        break;
      case COMPANY_PID_FIELD_NAME:
        parameterBuilder.companyPids(extractParamValues(queryParametersMap, field));
        break;
      case COMPANY_NAME_FIELD_NAME:
        parameterBuilder.companyName(extractParamValue(queryParametersMap, field));
        break;
      case NAME_FIELD_NAME:
        parameterBuilder.name(extractParamValue(queryParametersMap, field));
        break;
      case GLOBAL_ALIAS_NAME_FIELD_NAME:
        parameterBuilder.globalAliasName(extractParamValue(queryParametersMap, field));
        break;
      case STATUS_FIELD_NAME:
        parameterBuilder.status(extractParamValues(queryParametersMap, field));
        break;
      default:
        throw new IllegalArgumentException("Unknown field ".concat(field.getName()));
    }
  }

  /**
   * Extracts value from a field. This method is for obtaining value from a single field, so if such
   * field contains more then one value, the first one will be used.
   *
   * @param parameterMap map that contains {@code qf} parameter values from request query string
   * @param field a single-value field for which value should be obtained from a given map
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
   * {@link com.nexage.admin.core.specification.SiteQueryFieldSpecification} for details.
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

  private static <T> Optional<Set<T>> asMultiValue(T[] fieldValues) {
    Optional<Set<T>> values = Optional.empty();
    if (fieldValues.length != 0) {
      Set<T> typeSet = Set.of(fieldValues);
      values = Optional.of(typeSet);
    }
    return values;
  }

  private static <T> Optional<T> asSingleValue(T[] values) {
    return Stream.of(values).findFirst();
  }
}
