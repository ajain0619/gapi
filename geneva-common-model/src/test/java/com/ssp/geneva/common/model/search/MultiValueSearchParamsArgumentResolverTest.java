package com.ssp.geneva.common.model.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

@ExtendWith(MockitoExtension.class)
class MultiValueSearchParamsArgumentResolverTest {

  private static final String QO = "qo";
  private static final String QF = "qf";
  private static final SearchQueryOperator DEFAULT_OPERATOR = SearchQueryOperator.AND;

  @Mock NativeWebRequest nativeWebRequest;
  @Mock HttpServletRequest servletRequest;
  @Mock MethodParameter methodParameter;
  @Mock MultiValueSearchParams annotation;
  @Mock WebDataBinder webDataBinder;
  @Mock WebDataBinderFactory webDataBinderFactory;
  @InjectMocks MultiValueSearchParamsArgumentResolver resolver;

  @BeforeEach
  public void setUp() {
    given(nativeWebRequest.getNativeRequest()).willReturn(servletRequest);
    given(methodParameter.getParameterAnnotation(MultiValueSearchParams.class))
        .willReturn(annotation);
    lenient().when(annotation.operator()).thenReturn(DEFAULT_OPERATOR);
    Class queryParamsClass = MultiValueQueryParams.class;
    given(methodParameter.getParameterType()).willReturn(queryParamsClass);
  }

  @Test
  void testDecodeWhenQoAndQfAreMissing() {
    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertTrue(parsed.getFields().isEmpty());
    assertEquals(DEFAULT_OPERATOR, parsed.getOperator());
  }

  @Test
  void testDecodeWhenQoIsMissing() {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
    expected.add("key1", "val 1");
    expected.add("key2", "val 3");

    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertEquals(expected, parsed.getFields());
    assertEquals(DEFAULT_OPERATOR, parsed.getOperator());
  }

  @Test
  void testDecodeWhenQfIsMissing() {
    lenient().when(servletRequest.getParameter(QO)).thenReturn("OR");

    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertTrue(parsed.getFields().isEmpty());

    assertEquals(SearchQueryOperator.OR, parsed.getOperator());
  }

  @Test
  void testDecodeWhenQoHasWrongFormat() {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    given(servletRequest.getParameter(QO)).willReturn("orrrr");

    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertEquals(DEFAULT_OPERATOR, parsed.getOperator());
  }

  @Test
  void testDecodeWhenQoIsLowercase() {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    given(servletRequest.getParameter(QO)).willReturn("or");
    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertEquals(SearchQueryOperator.OR, parsed.getOperator());
  }

  @Test
  void testDecodeWhenQoContainsWhitecharacters() {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    given(servletRequest.getParameter(QO)).willReturn("    or   ");
    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
    assertEquals(SearchQueryOperator.OR, parsed.getOperator());
  }

  @Test
  void shouldVerifyValidConstraintInvoked() throws Exception {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    given(servletRequest.getParameter(QO)).willReturn("    or   ");
    given(
            webDataBinderFactory.createBinder(
                any(NativeWebRequest.class), any(Object.class), any(String.class)))
        .willReturn(webDataBinder);
    given(methodParameter.getParameterAnnotations())
        .willReturn(
            new Annotation[] {
              new Valid() {
                @Override
                public Class<? extends Annotation> annotationType() {
                  return Valid.class;
                }
              }
            });
    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, webDataBinderFactory);
    verify(webDataBinder).validate(nullable(Object.class));
    assertNotNull(parsed);
  }

  @Test
  void shouldVerifyValidConstraintNotInvoked() throws Exception {
    given(servletRequest.getParameter(QF)).willReturn("{key1=val 1,key2=val 3}");
    given(servletRequest.getParameter(QO)).willReturn("    or   ");
    given(
            webDataBinderFactory.createBinder(
                any(NativeWebRequest.class), any(Object.class), any(String.class)))
        .willReturn(webDataBinder);

    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, webDataBinderFactory);
    verify(webDataBinder, times(0)).validate(nullable(Object.class));
    assertNotNull(parsed);
  }

  @Test
  void shouldNotThrowExceptionWhenGetParameterAnnotationReturnsNull() {
    given(methodParameter.getParameterAnnotation(MultiValueSearchParams.class)).willReturn(null);
    MultiValueQueryParams parsed =
        (MultiValueQueryParams)
            resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);
    assertNotNull(parsed);
  }
}
