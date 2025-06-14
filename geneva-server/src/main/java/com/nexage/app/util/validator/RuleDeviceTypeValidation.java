package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.DeviceType;
import com.nexage.admin.core.repository.DeviceTypeRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleDeviceTypeValidation implements RuleTargetValidation {

  final DeviceTypeRepository deviceTypeRepository;

  @Autowired
  public RuleDeviceTypeValidation(DeviceTypeRepository deviceTypeRepository) {
    this.deviceTypeRepository = deviceTypeRepository;
  }

  @Override
  public boolean isValid(String data) {
    List<String> commaSeparatedData = Arrays.stream(data.split(",")).collect(Collectors.toList());
    List<DeviceType> resultList = deviceTypeRepository.findByNameIn(commaSeparatedData);

    return commaSeparatedData.size() == resultList.size();
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.DEVICE_TYPE;
  }
}
