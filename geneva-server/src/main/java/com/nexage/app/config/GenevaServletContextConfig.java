package com.nexage.app.config;

import com.codahale.metrics.servlet.InstrumentedFilter;
import com.nexage.app.config.security.SecureHeadersFilter;
import com.ssp.geneva.common.cache.SinglePageCachingFilterNoQueryParams;
import com.ssp.geneva.common.logging.MDCFilter;
import java.util.Map;
import javax.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class GenevaServletContextConfig {

  @Bean("requestContextFilter")
  public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
    var registration = new FilterRegistrationBean<>(new RequestContextFilter());
    registration.addUrlPatterns("/*");
    registration.setName("requestContextFilter");
    registration.setOrder(SessionRepositoryFilter.DEFAULT_ORDER + 1);
    return registration;
  }

  @Bean("multipartProperties")
  public MultipartProperties multipartProperties() {
    var multipartProperties = new MultipartProperties();
    var maxReqFileSize = DataSize.of(30, DataUnit.MEGABYTES);
    multipartProperties.setEnabled(true);
    multipartProperties.setMaxFileSize(maxReqFileSize);
    multipartProperties.setMaxRequestSize(maxReqFileSize);
    return multipartProperties;
  }

  @Bean("mdcFilter")
  public FilterRegistrationBean<MDCFilter> mdcFilterRegistration() {
    var filterRegistrationBean = new FilterRegistrationBean<>(new MDCFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    filterRegistrationBean.setName("mdcFilter");
    return filterRegistrationBean;
  }

  @Bean("etagFilter")
  public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilterRegistration() {
    var filterRegistrationBean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    filterRegistrationBean.setName("etagFilter");
    return filterRegistrationBean;
  }

  @Bean("dealSupplierCacheFilter")
  public FilterRegistrationBean<SinglePageCachingFilterNoQueryParams> dealSupplierCacheFilter() {
    var registrationBean = new FilterRegistrationBean<>(new SinglePageCachingFilterNoQueryParams());
    registrationBean.setInitParameters(
        Map.of(
            "cacheName", "dealSupplierCacheFilter",
            "suppressStackTrace", "false"));
    registrationBean.addUrlPatterns("/deals/suppliers/*");
    registrationBean.setDispatcherTypes(
        DispatcherType.REQUEST, DispatcherType.INCLUDE, DispatcherType.FORWARD);
    registrationBean.setName("dealSupplierCacheFilter");
    return registrationBean;
  }

  @Bean("instrumentedFilter")
  public FilterRegistrationBean<InstrumentedFilter> instrumentedFilterRegistration() {
    var filterRegistrationBean = new FilterRegistrationBean<>(new InstrumentedFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    filterRegistrationBean.setName("instrumentedFilter");
    return filterRegistrationBean;
  }

  @Bean("securityHeadersFilter")
  public FilterRegistrationBean<SecureHeadersFilter> securityHeadersFilterRegistration() {
    var filterRegistrationBean = new FilterRegistrationBean<>(new SecureHeadersFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    filterRegistrationBean.setName("securityHeadersFilter");
    return filterRegistrationBean;
  }
}
