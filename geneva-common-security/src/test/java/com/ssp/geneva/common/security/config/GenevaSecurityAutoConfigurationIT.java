package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.security.config.GenevaSecurityAutoConfigurationIT.TestApplicationProperties;
import com.ssp.geneva.common.security.filter.sso.SingleSignOnSessionFilter;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ActiveProfiles("aws")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaSecurityAutoConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class GenevaSecurityAutoConfigurationIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldConfig() {
    var singleSignOnSessionFilter =
        (SingleSignOnSessionFilter) context.getBean("singleSignOnSessionFilter");
    assertNotNull(singleSignOnSessionFilter);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean({"coreDS", "ssp.geneva.api.datasource.core", "dataSource"})
    public DataSource dataSource() {
      return mock(DataSource.class);
    }

    @Bean("sysConfigUtil")
    public SysConfigUtil sysConfigUtil() {
      return mock(SysConfigUtil.class);
    }

    @Bean("oneCentralSdkClient")
    public OneCentralSdkClient oneCentralSdkClient() {
      return mock(OneCentralSdkClient.class);
    }

    @Bean("userRepository")
    public UserRepository userRepository() {
      return mock(UserRepository.class);
    }

    @Bean("companyRepository")
    public CompanyRepository companyRepository() {
      return mock(CompanyRepository.class);
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean("messageHandler")
    public MessageHandler messageHandler() {
      return mock(MessageHandler.class);
    }

    @Bean("coreNamedJdbcTemplate")
    public NamedParameterJdbcOperations coreNamedJdbcTemplate() {
      return mock(NamedParameterJdbcOperations.class);
    }

    @Bean("handlerExceptionResolver")
    public HandlerExceptionResolver handlerExceptionResolver() {
      return mock(HandlerExceptionResolver.class);
    }
  }
}
