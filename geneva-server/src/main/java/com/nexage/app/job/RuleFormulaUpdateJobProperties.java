package com.nexage.app.job;

import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
@Getter
public class RuleFormulaUpdateJobProperties {

  private static final String PF_UPDATE_JOB_CRON_SCHEDULE_KEY = "geneva.pf.update.job.cron";
  private static final String PF_UPDATE_JOB_USERNAME_KEY = "geneva.pf.update.job.username";

  private static final String PF_UPDATE_JOB_CRON_SCHEDULE_DEFAULT_VALUE = "0 0 2 * * ?";
  private static final String PF_UPDATE_JOB_USERNAME_DEFAULT_VALUE = "";

  private final String ruleFormulaJobUsername;
  private final String ruleFormulaJobCronSchedule;

  private final PhoneCastConfigurationRepository phoneCastConfigurationRepository;

  public RuleFormulaUpdateJobProperties(
      PhoneCastConfigurationRepository phoneCastConfigurationRepository) {
    this.phoneCastConfigurationRepository = phoneCastConfigurationRepository;
    this.ruleFormulaJobUsername =
        getConfigValue(PF_UPDATE_JOB_USERNAME_KEY, PF_UPDATE_JOB_USERNAME_DEFAULT_VALUE);
    this.ruleFormulaJobCronSchedule =
        getConfigValue(PF_UPDATE_JOB_CRON_SCHEDULE_KEY, PF_UPDATE_JOB_CRON_SCHEDULE_DEFAULT_VALUE);
  }

  private String getConfigValue(String configKey, String defaultValue) {
    var value = phoneCastConfigurationRepository.findByConfigKey(configKey);
    if (value.isPresent() && !StringUtils.isEmpty(value.get().getConfigValue())) {
      return value.get().getConfigValue();
    }
    return defaultValue;
  }
}
