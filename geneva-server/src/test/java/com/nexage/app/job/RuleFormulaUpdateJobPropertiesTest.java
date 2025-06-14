package com.nexage.app.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.PhoneCastConfiguration;
import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleFormulaUpdateJobPropertiesTest {

  @Mock private PhoneCastConfigurationRepository phoneCastConfigurationRepository;

  private RuleFormulaUpdateJobProperties ruleFormulaUpdateJobProperties;

  @Test
  void shouldReturnDefaultValueForNonExistingJobUsername() {
    // given
    final var usernameConfigKey = "geneva.pf.update.job.username";
    final var cronConfigKey = "geneva.pf.update.job.cron";
    when(phoneCastConfigurationRepository.findByConfigKey(usernameConfigKey))
        .thenReturn(Optional.empty());
    when(phoneCastConfigurationRepository.findByConfigKey(cronConfigKey))
        .thenReturn(Optional.empty());
    ruleFormulaUpdateJobProperties =
        new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);

    // when
    var result = ruleFormulaUpdateJobProperties.getRuleFormulaJobUsername();

    // then
    assertEquals("", result);
    verify(phoneCastConfigurationRepository).findByConfigKey(usernameConfigKey);
  }

  @Test
  void shouldReturnDefaultValueForEmptyJobUsername() {
    // given
    final var usernameConfigKey = "geneva.pf.update.job.username";
    final var cronConfigKey = "geneva.pf.update.job.cron";
    final var usernameConfigValue = "";
    final var cronConfigValue = "* * * * * ?";
    var usernamePhoneCastConfiguration = mock(PhoneCastConfiguration.class);
    var cronPhoneCastConfiguration = mock(PhoneCastConfiguration.class);

    when(usernamePhoneCastConfiguration.getConfigValue()).thenReturn(usernameConfigValue);
    when(cronPhoneCastConfiguration.getConfigValue()).thenReturn(cronConfigValue);
    when(phoneCastConfigurationRepository.findByConfigKey(usernameConfigKey))
        .thenReturn(Optional.of(usernamePhoneCastConfiguration));
    when(phoneCastConfigurationRepository.findByConfigKey(cronConfigKey))
        .thenReturn(Optional.of(cronPhoneCastConfiguration));
    ruleFormulaUpdateJobProperties =
        new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);

    // when
    var result = ruleFormulaUpdateJobProperties.getRuleFormulaJobUsername();

    // then
    assertEquals("", result);
    verify(phoneCastConfigurationRepository).findByConfigKey(usernameConfigKey);
  }

  @Test
  void shouldReturnDefaultValueForNonExistingCron() {
    // given
    final var usernameConfigKey = "geneva.pf.update.job.username";
    final var cronConfigKey = "geneva.pf.update.job.cron";
    when(phoneCastConfigurationRepository.findByConfigKey(usernameConfigKey))
        .thenReturn(Optional.empty());
    when(phoneCastConfigurationRepository.findByConfigKey(cronConfigKey))
        .thenReturn(Optional.empty());
    ruleFormulaUpdateJobProperties =
        new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);

    // when
    var result = ruleFormulaUpdateJobProperties.getRuleFormulaJobCronSchedule();

    // then
    assertEquals("0 0 2 * * ?", result);
    verify(phoneCastConfigurationRepository).findByConfigKey(cronConfigKey);
  }

  @Test
  void shouldReturnDefaultValueForEmptyCron() {
    // given
    final var usernameConfigKey = "geneva.pf.update.job.username";
    final var cronConfigKey = "geneva.pf.update.job.cron";
    final var usernameConfigValue = "svc-geneva-api";
    final var cronConfigValue = "";
    var usernamePhoneCastConfiguration = mock(PhoneCastConfiguration.class);
    var cronPhoneCastConfiguration = mock(PhoneCastConfiguration.class);

    when(usernamePhoneCastConfiguration.getConfigValue()).thenReturn(usernameConfigValue);
    when(cronPhoneCastConfiguration.getConfigValue()).thenReturn(cronConfigValue);
    when(phoneCastConfigurationRepository.findByConfigKey(usernameConfigKey))
        .thenReturn(Optional.of(usernamePhoneCastConfiguration));
    when(phoneCastConfigurationRepository.findByConfigKey(cronConfigKey))
        .thenReturn(Optional.of(cronPhoneCastConfiguration));
    ruleFormulaUpdateJobProperties =
        new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);

    // when
    var result = ruleFormulaUpdateJobProperties.getRuleFormulaJobCronSchedule();

    // then
    assertEquals("0 0 2 * * ?", result);
    verify(phoneCastConfigurationRepository).findByConfigKey(cronConfigKey);
  }

  @Test
  void shouldReturnExpectedValues() {
    // given
    final var usernameConfigKey = "geneva.pf.update.job.username";
    final var cronConfigKey = "geneva.pf.update.job.cron";
    final var usernameConfigValue = "svc-geneva-api";
    final var cronConfigValue = "* * * * * ?";
    var usernamePhoneCastConfiguration = mock(PhoneCastConfiguration.class);
    var cronPhoneCastConfiguration = mock(PhoneCastConfiguration.class);

    when(usernamePhoneCastConfiguration.getConfigValue()).thenReturn(usernameConfigValue);
    when(cronPhoneCastConfiguration.getConfigValue()).thenReturn(cronConfigValue);
    when(phoneCastConfigurationRepository.findByConfigKey(usernameConfigKey))
        .thenReturn(Optional.of(usernamePhoneCastConfiguration));
    when(phoneCastConfigurationRepository.findByConfigKey(cronConfigKey))
        .thenReturn(Optional.of(cronPhoneCastConfiguration));
    ruleFormulaUpdateJobProperties =
        new RuleFormulaUpdateJobProperties(phoneCastConfigurationRepository);

    // when
    var usernameResult = ruleFormulaUpdateJobProperties.getRuleFormulaJobUsername();
    var cronResult = ruleFormulaUpdateJobProperties.getRuleFormulaJobCronSchedule();

    // then
    assertNotNull(usernameResult);
    assertEquals(usernameConfigValue, usernameResult);
    verify(phoneCastConfigurationRepository).findByConfigKey(usernameConfigKey);
    assertNotNull(cronResult);
    assertEquals(cronConfigValue, cronResult);
    verify(phoneCastConfigurationRepository).findByConfigKey(cronConfigKey);
  }
}
