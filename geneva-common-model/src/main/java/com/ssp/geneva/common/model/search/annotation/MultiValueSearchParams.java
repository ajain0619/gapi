package com.ssp.geneva.common.model.search.annotation;

import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should be bound to a web request multi value
 * search parameters named <code>qf</code> and <code>qo</code>.
 *
 * <p>Requirements are as follows:
 *
 * <ul>
 *   <li>If <code>qo</code> parameter is not present in a web request its default value will be
 *       taken from {@link #operator()} field
 *   <li><code>qf</code> parameter has to have structure like <code>
 * /search?qf={key1=val1, kay2=val3|val4, key3=val5|val6}</code> otherwise can be wrongly parsed
 * </ul>
 *
 * <p>Method parameter to be used with this annotation has to be of type {@link
 * com.ssp.geneva.common.model.search.MultiValueQueryParams}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MultiValueSearchParams {

  SearchQueryOperator operator() default SearchQueryOperator.AND;
}
