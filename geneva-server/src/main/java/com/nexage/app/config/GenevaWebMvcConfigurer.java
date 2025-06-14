package com.nexage.app.config;

import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.ssp.geneva.common.model.search.MultiValueSearchParamsArgumentResolver;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Log4j2
@Configuration
@EnableSpringDataWebSupport
public class GenevaWebMvcConfigurer implements WebMvcConfigurer {

  private Map<String, String> swaggerResourceMappings;

  public GenevaWebMvcConfigurer(
      @Autowired(required = false) @Qualifier("swaggerResourceMappings")
          Map<String, String> swaggerResourceMappings) {
    log.info("GenevaWebMvcConfigurer:init");
    this.swaggerResourceMappings = swaggerResourceMappings;
  }

  @Bean("customViewLayerObjectMapper")
  public CustomViewLayerObjectMapper customViewLayerObjectMapper() {
    return GenevaServerJacksonBeanFactory.initCustomViewLayerObjectMapper();
  }

  @Bean("customObjectMapper")
  public CustomObjectMapper objectMapper() {
    return GenevaServerJacksonBeanFactory.initCustomObjectMapper();
  }

  @Bean("sysConfigUtil")
  @ConditionalOnClass({GlobalConfigService.class, Properties.class})
  @ConditionalOnBean(name = {"globalConfigService", "environment"})
  public SysConfigUtil sysConfigUtil(
      @Autowired GlobalConfigService globalConfigService, @Autowired Environment environment) {
    return new SysConfigUtil(globalConfigService, environment);
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    log.debug("adding MappingJackson2HttpMessageConverter");
    return new MappingJackson2HttpMessageConverter(customViewLayerObjectMapper());
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (swaggerResourceMappings != null) {
      swaggerResourceMappings.forEach(
          (key, value) -> registry.addResourceHandler(key).addResourceLocations(value));
    }
    registry.addResourceHandler("/", "/resources/*").addResourceLocations("/resources/**");
    registry.addResourceHandler("/static/*").addResourceLocations("classpath:/static/json/");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new PageableHandlerMethodArgumentResolver());
    resolvers.add(new MultiValueSearchParamsArgumentResolver());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    var localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    registry.addInterceptor(localeChangeInterceptor);
  }
}
