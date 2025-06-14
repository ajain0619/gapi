package com.ssp.geneva.common.model.search;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.util.MultiValueMap;

/**
 * Representation of multiple values qf and qo search parameters combined into one object for
 * simpler use.
 *
 * <p>Should be used together with {@link
 * com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams} annotation to parse
 * incoming web request properly. <br>
 * The lombok annotations on this class are pretty much the same as {@code @Value}. However,
 * {@code @Value} has not bean used in order to not make this class final so other class can extend
 * this one.
 */
@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class MultiValueQueryParams {

  MultiValueMap<String, String> fields;

  SearchQueryOperator operator;
}
