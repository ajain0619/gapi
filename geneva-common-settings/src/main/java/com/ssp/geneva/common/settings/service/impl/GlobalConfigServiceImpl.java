package com.ssp.geneva.common.settings.service.impl;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.GlobalConfig;
import com.nexage.admin.core.repository.GlobalConfigRepository;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GlobalConfigServiceImpl implements GlobalConfigService {

  private final GlobalConfigRepository globalConfigRepository;

  /** {@inheritDoc} */
  @Override
  public String getStringValue(GlobalConfigProperty property) {
    return globalConfigRepository
        .findByProperty(property)
        .map(GlobalConfig::getStringValue)
        .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Integer getIntegerValue(GlobalConfigProperty property) {
    return globalConfigRepository
        .findByProperty(property)
        .map(GlobalConfig::getIntegerValue)
        .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Boolean getBooleanValue(GlobalConfigProperty property) {
    return globalConfigRepository
        .findByProperty(property)
        .map(GlobalConfig::getBooleanValue)
        .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public List<Long> getLongListValue(GlobalConfigProperty property) {
    return globalConfigRepository
        .findByProperty(property)
        .map(GlobalConfig::getLongListValue)
        .orElse(List.of());
  }
}
