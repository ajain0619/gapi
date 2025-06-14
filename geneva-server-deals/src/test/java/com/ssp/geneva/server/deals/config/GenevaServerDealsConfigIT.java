package com.ssp.geneva.server.deals.config;

import static org.mockito.Mockito.mock;

import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.ssp.geneva.server.deals.config.GenevaServerDealsConfigIT.TestApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, GenevaServerDealsConfig.class})
@TestPropertySource("classpath:application-test.properties")
class GenevaServerDealsConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    // TODO: Add valid service beans for verification
    // assertNotNull(context.getBean("dealInventoryGridServiceImpl"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("directDealRepository")
    public DirectDealRepository directDealRepository() {
      return mock(DirectDealRepository.class);
    }

    @Bean("dealPublisherRepository")
    public DealPublisherRepository dealPublisherRepository() {
      return mock(DealPublisherRepository.class);
    }
  }
}
