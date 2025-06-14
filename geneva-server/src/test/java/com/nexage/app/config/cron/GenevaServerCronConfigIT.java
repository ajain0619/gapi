package com.nexage.app.config.cron;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.PhoneCastConfiguration;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.app.config.cron.GenevaServerCronConfigIT.TestApplicationProperties;
import com.nexage.app.services.DirectDealService;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.PublisherRuleService;
import com.nexage.app.services.impl.AWSFilesystemService;
import com.nexage.app.services.support.DealServiceSupport;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, GenevaServerCronConfig.class})
@TestPropertySource(
    properties = {
      "spring.config.location=classpath:application.properties",
      "placementformulaUpdateJob.enable=false",
    })
class GenevaServerCronConfigIT {
  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var ruleFormulaJobProperties = context.getBean("ruleFormulaJobProperties");
    assertNotNull(ruleFormulaJobProperties);

    var ruleFormulaAutoUpdateJobTrigger = context.getBean("ruleFormulaAutoUpdateJobTrigger");
    assertNotNull(ruleFormulaAutoUpdateJobTrigger);

    var ruleFormulaUpdateService = context.getBean("ruleFormulaUpdateService");
    assertNotNull(ruleFormulaUpdateService);

    var placementFormulaAutoUpdateJob = context.getBean("placementFormulaAutoUpdateJob");
    assertNotNull(placementFormulaAutoUpdateJob);

    var schedulerFactory = context.getBean("schedulerFactory");
    assertNotNull(schedulerFactory);

    var ruleFormulaAutoUpdateJob = context.getBean("ruleFormulaAutoUpdateJob");
    assertNotNull(ruleFormulaAutoUpdateJob);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean(value = "phoneCastConfigurationRepository")
    public PhoneCastConfigurationRepository phoneCastConfigurationRepository() {
      var repository = mock(PhoneCastConfigurationRepository.class);
      var cronConfiguration = new PhoneCastConfiguration();
      cronConfiguration.setConfigKey("geneva.pf.update.job.cron");
      cronConfiguration.setConfigValue("* * * * * ?");
      var usernameConfiguration = new PhoneCastConfiguration();
      usernameConfiguration.setConfigKey("geneva.pf.update.job.username");
      usernameConfiguration.setConfigValue("svc-potato");
      when(repository.findByConfigKey(cronConfiguration.getConfigKey()))
          .thenReturn(Optional.of(cronConfiguration));
      when(repository.findByConfigKey(usernameConfiguration.getConfigKey()))
          .thenReturn(Optional.of(usernameConfiguration));

      return repository;
    }

    @Bean(value = "ruleFormulaAutoUpdateJob")
    public JobDetail ruleFormulaAutoUpdateJob() {
      return mock(JobDetail.class);
    }

    @Bean(value = "genevaDataFileSystemService")
    public FileSystemService fileSystemService() {
      return mock(AWSFilesystemService.class);
    }

    @Bean(value = "userDetailsService")
    public UserDetailsService userDetailsService() {
      return mock(UserDetailsService.class);
    }

    @Bean(value = "placementFormulaAssembler")
    public PlacementFormulaAssembler placementFormulaAssembler() {
      return mock(PlacementFormulaAssembler.class);
    }

    @Bean(value = "ruleDeployedPositionRepository")
    public RuleDeployedPositionRepository ruleDeployedPositionRepository() {
      return mock(RuleDeployedPositionRepository.class);
    }

    @Bean(value = "directDealService")
    public DirectDealService directDealService() {
      return mock(DirectDealService.class);
    }

    @Bean(value = "directDealRepository")
    public DirectDealRepository directDealRepository() {
      return mock(DirectDealRepository.class);
    }

    @Bean(value = "RuleRepository")
    public RuleRepository ruleRepository() {
      return mock(RuleRepository.class);
    }

    @Bean(value = "RuleFormulaPositionViewRepository")
    public RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository() {
      return mock(RuleFormulaPositionViewRepository.class);
    }

    @Bean(value = "publisherRuleService")
    public PublisherRuleService publisherRuleService() {
      return mock(PublisherRuleService.class);
    }

    @Bean(value = "dealServiceSupport")
    public DealServiceSupport dealServiceSupport() {
      return mock(DealServiceSupport.class);
    }
  }
}
