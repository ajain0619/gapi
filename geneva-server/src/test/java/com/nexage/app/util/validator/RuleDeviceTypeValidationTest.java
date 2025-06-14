package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.DeviceType;
import com.nexage.admin.core.repository.DeviceTypeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleDeviceTypeValidationTest {
  @Mock DeviceTypeRepository deviceTypeRepository;

  @InjectMocks private RuleDeviceTypeValidation ruleDeviceTypeValidation;

  @Test
  void shouldTestEmptyListValidation() {
    Mockito.when(deviceTypeRepository.findByNameIn(any())).thenReturn(List.of());
    assertFalse(ruleDeviceTypeValidation.isValid("Phone"));
  }

  @Test
  void shouldTestValidListValidation() {
    DeviceType deviceType = new DeviceType(1L, 2, "Phone");
    Mockito.when(deviceTypeRepository.findByNameIn(any())).thenReturn(List.of(deviceType));
    assertTrue(ruleDeviceTypeValidation.isValid("Phone"));
  }

  @Test
  void shouldTestRuleTarget() {
    assertEquals(RuleTargetType.DEVICE_TYPE, ruleDeviceTypeValidation.getRuleTarget());
  }
}
