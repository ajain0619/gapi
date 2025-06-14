package com.nexage.app.config.cron;

import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.app.job.PlacementFormulaAutoUpdateJob;
import com.nexage.app.job.PlacementFormulaUpdateService;
import com.nexage.app.job.RuleFormulaUpdateJobProperties;
import com.nexage.app.job.RuleFormulaUpdateService;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;

@Log4j2
@Configuration
public class GenevaServerCronConfig {

  @Value("${placementformulaUpdateJob.enable}")
  private boolean placementformulaUpdateJobEnabled;

  public GenevaServerCronConfig() {
    log.info("geneva.auto-config:GenevaServerCronConfig");
  }

  @Bean("ruleFormulaJobProperties")
  public RuleFormulaUpdateJobProperties ruleFormulaJobProperties(
      @Autowired PhoneCastConfigurationRepository phoneCastConfigurationRepository) {
    return new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);
  }

  @Bean("ruleFormulaAutoUpdateJobTrigger")
  public CronTriggerFactoryBean ruleFormulaAutoUpdateJobTrigger(
      JobDetail ruleFormulaAutoUpdateJob, RuleFormulaUpdateJobProperties properties) {
    var cronTriggerFactoryBean = new CronTriggerFactoryBean();
    cronTriggerFactoryBean.setCronExpression(properties.getRuleFormulaJobCronSchedule());
    cronTriggerFactoryBean.setJobDetail(ruleFormulaAutoUpdateJob);
    return cronTriggerFactoryBean;
  }

  @Bean("ruleFormulaUpdateService")
  public RuleFormulaUpdateService ruleFormulaUpdateService(
      @Autowired RuleRepository ruleRepository,
      @Autowired RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository,
      @Autowired PlacementFormulaAssembler placementFormulaAssembler,
      @Autowired RuleDeployedPositionRepository ruleDeployedPositionRepository) {
    return new RuleFormulaUpdateService(
        ruleRepository,
        ruleFormulaPositionViewRepository,
        placementFormulaAssembler,
        ruleDeployedPositionRepository);
  }

  @Bean("placementFormulaAutoUpdateJob")
  public PlacementFormulaAutoUpdateJob placementFormulaAutoUpdateJob(
      @Autowired UserDetailsService userDetailsService,
      @Autowired PlacementFormulaUpdateService ruleFormulaUpdateService,
      @Autowired RuleFormulaUpdateJobProperties properties) {
    return new PlacementFormulaAutoUpdateJob(
        userDetailsService, ruleFormulaUpdateService, properties.getRuleFormulaJobUsername());
  }

  @Bean("schedulerFactory")
  public SchedulerFactoryBean schedulerFactory(
      @Autowired CronTriggerFactoryBean cronTriggerFactoryBean) {
    var schedulerFactoryBean = new SchedulerFactoryBean();
    if (placementformulaUpdateJobEnabled) {
      schedulerFactoryBean.setTriggers(cronTriggerFactoryBean.getObject());
    } else {
      schedulerFactoryBean.setTriggers();
    }
    return schedulerFactoryBean;
  }

  @Bean("ruleFormulaAutoUpdateJob")
  public MethodInvokingJobDetailFactoryBean ruleFormulaAutoUpdateJob(
      @Autowired PlacementFormulaAutoUpdateJob placementFormulaAutoUpdateJob) {
    var methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
    methodInvokingJobDetailFactoryBean.setTargetObject(placementFormulaAutoUpdateJob);
    methodInvokingJobDetailFactoryBean.setTargetMethod("runJob");
    return methodInvokingJobDetailFactoryBean;
  }
}
