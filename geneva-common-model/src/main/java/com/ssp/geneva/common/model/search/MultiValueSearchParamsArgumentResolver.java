package com.ssp.geneva.common.model.search;

import static java.util.Objects.nonNull;

import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import com.ssp.geneva.common.model.search.util.MapParamDecoder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Extracts multi value search parameters from web requests. Those params are stored in <code>qf
 * </code> and <code>qo</code> query parameters.
 *
 * <p>Examples of urls that will be parsed correctly:
 *
 * <ul>
 *   <li>/search?qf={key1=val1, kay2=val3|val4, key3=val5|val6}
 *   <li>/search?qf={key1=val1, kay2=val3}&qo=OR
 * </ul>
 */
public class MultiValueSearchParamsArgumentResolver implements HandlerMethodArgumentResolver {

  private static final String QUERY_FIELDS = "qf";
  private static final String QUERY_OPERATOR = "qo";

  /**
   * Whether the given {@link MethodParameter method parameter} is supported by this resolver.
   *
   * @param methodParameter the method parameter to check
   * @return true if this resolver supports the supplied parameter; false otherwise
   */
  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return nonNull(methodParameter.getParameterAnnotation(MultiValueSearchParams.class));
  }

  /**
   * Produces parsed query as {@link MultiValueQueryParams}.
   *
   * @param methodParameter the method parameter to resolve. This parameter must have previously
   *     been passed to {@link #supportsParameter(MethodParameter)} which must have returned true.
   * @param modelAndViewContainer the {@link ModelAndViewContainer} for the current request
   * @param nativeWebRequest the current request
   * @param webDataBinderFactory a factory for creating {@link
   *     org.springframework.web.bind.WebDataBinder} instances
   * @return the resolved argument value, or null if not resolvable
   */
  @Override
  public MultiValueQueryParams resolveArgument(
      MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest,
      WebDataBinderFactory webDataBinderFactory) {

    HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
    MultiValueMap<String, String> queryFields = parseQueryFields(request);
    SearchQueryOperator queryOperator = parseSearchOperator(methodParameter, request);
    var multiValueQueryParams = createQueryParams(queryFields, queryOperator, methodParameter);
    try {
      var binder =
          webDataBinderFactory.createBinder(
              nativeWebRequest, multiValueQueryParams, "MultiValueQueryParams");
      Annotation[] annotations = methodParameter.getParameterAnnotations();
      if (ArrayUtils.getLength(annotations) > 0) {
        Arrays.stream(annotations)
            .filter(ann -> ann.annotationType().getSimpleName().startsWith("Valid"))
            .findFirst()
            .ifPresent(
                ann -> {
                  Object hints = AnnotationUtils.getValue(ann);
                  binder.validate(
                      hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
                });
      }
    } catch (Exception e) {
      // do nothing as it invokes validation on target if presents
    }

    return multiValueQueryParams;
  }

  @SneakyThrows
  public <P extends MultiValueQueryParams> P createQueryParams(
      MultiValueMap<String, String> queryFields,
      SearchQueryOperator queryOperator,
      MethodParameter methodParameter) {
    Class<?> queryParamsClass = methodParameter.getParameterType();
    if (!MultiValueQueryParams.class.isAssignableFrom(queryParamsClass)) {
      queryParamsClass = MultiValueQueryParams.class;
    }
    Constructor<?> constructor =
        queryParamsClass.getConstructor(MultiValueMap.class, SearchQueryOperator.class);
    return (P) constructor.newInstance(queryFields, queryOperator);
  }

  private MultiValueMap<String, String> parseQueryFields(HttpServletRequest request) {
    String qf = request.getParameter(QUERY_FIELDS);

    return MapParamDecoder.decodeQueryParam(qf);
  }

  private SearchQueryOperator parseSearchOperator(
      MethodParameter methodParameter, HttpServletRequest request) {
    MultiValueSearchParams annotation =
        methodParameter.getParameterAnnotation(MultiValueSearchParams.class);
    SearchQueryOperator queryOperator = null;
    if (null != annotation) {
      queryOperator = annotation.operator();
    }
    String qo = request.getParameter(QUERY_OPERATOR);
    try {
      if (nonNull(qo)) {
        queryOperator = SearchQueryOperator.valueOf(qo.trim().toUpperCase());
      }
    } catch (IllegalArgumentException ex) {
      // fall through
      // if query operator cannot be obtained from the request default to the one specified in the
      // annotation
    }

    return queryOperator;
  }
}
