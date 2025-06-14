package com.ssp.geneva.server.report.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.ExchangeConfigRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.countryservice.CountryService;
import com.ssp.geneva.server.report.config.GenevaServerReportConfigIT.TestApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, GenevaServerReportConfig.class})
@TestPropertySource("classpath:application-test.properties")
class GenevaServerReportConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedPerformanceBeans() {
    assertNotNull(context.getBean("biddersPerformance"));
    assertNotNull(context.getBean("estimatedRevenue"));
    assertNotNull(context.getBean("biddersPerformanceFacade"));
    assertNotNull(context.getBean("estimatedRevenueFacade"));
  }

  @Test
  void shouldRegisterExpectedReportsBeans() {
    assertNotNull(context.getBean("bidderSpendReport"));
    assertNotNull(context.getBean("subscriptionDataUsageReport"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("coreNamedJdbcTemplate")
    public NamedParameterJdbcTemplate coreNamedJdbcTemplate() {
      return mock(NamedParameterJdbcTemplate.class);
    }

    @Bean("coreServicesJdbcTemplate")
    public JdbcTemplate coreServicesJdbcTemplate() {
      return mock(JdbcTemplate.class);
    }

    @Bean("dwNamedJdbcTemplate")
    public NamedParameterJdbcTemplate dwNamedJdbcTemplate() {
      return mock(NamedParameterJdbcTemplate.class);
    }

    @Bean("companyRepository")
    public CompanyRepository companyRepository() {
      return mock(CompanyRepository.class);
    }

    @Bean("directDealRepository")
    public DirectDealRepository directDealRepository() {
      return mock(DirectDealRepository.class);
    }

    @Bean("siteRepository")
    public SiteRepository siteRepository() {
      return mock(SiteRepository.class);
    }

    @Bean("bidderConfigRepository")
    public BidderConfigRepository bidderConfigRepository() {
      return mock(BidderConfigRepository.class);
    }

    @Bean("phoneCastConfigService")
    public PhoneCastConfigService phoneCastConfigService() {
      return mock(PhoneCastConfigService.class);
    }

    @Bean("exchangeConfigRepository")
    public ExchangeConfigRepository exchangeConfigRepository() {
      return mock(ExchangeConfigRepository.class);
    }

    @Bean("countryService")
    public CountryService countryService() {
      return mock(CountryService.class);
    }
  }
}
