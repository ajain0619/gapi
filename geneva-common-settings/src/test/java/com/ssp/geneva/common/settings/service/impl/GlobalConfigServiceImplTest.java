package com.ssp.geneva.common.settings.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.GlobalConfig;
import com.nexage.admin.core.repository.GlobalConfigRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GlobalConfigServiceImplTest {

  @Mock private GlobalConfigRepository globalConfigRepository;
  @InjectMocks private GlobalConfigServiceImpl globalConfigService;

  @Test
  void shouldReturnStringValueWhenPropertyExists() {
    // given
    var config = mock(GlobalConfig.class);
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    var value = "value";
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.of(config));
    when(config.getStringValue()).thenReturn(value);

    // when
    String returnedValue = globalConfigService.getStringValue(property);

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldReturnNullWhenStringPropertyDoesNotExist() {
    // given
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.empty());

    // when
    String returnedValue = globalConfigService.getStringValue(property);

    // then
    assertNull(returnedValue);
  }

  @Test
  void shouldReturnIntegerValueWhenPropertyExistsAndIsInteger() {
    // given
    var config = mock(GlobalConfig.class);
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    var value = 123;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.of(config));
    when(config.getIntegerValue()).thenReturn(value);

    // when
    Integer returnedValue = globalConfigService.getIntegerValue(property);

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldReturnNullWhenIntegerPropertyDoesNotExist() {
    // given
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.empty());

    // when
    Integer returnedValue = globalConfigService.getIntegerValue(property);

    // then
    assertNull(returnedValue);
  }

  @Test
  void shouldReturnNullOnGetIntegerValueWhenPropertyExistsButIsNotInteger() {
    // given
    var config = mock(GlobalConfig.class);
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.of(config));
    when(config.getIntegerValue()).thenReturn(null);

    // when
    Integer returnedValue = globalConfigService.getIntegerValue(property);

    // then
    assertNull(returnedValue);
  }

  @Test
  void shouldReturnBooleanValueWhenPropertyExistsAndIsBoolean() {
    // given
    var config = mock(GlobalConfig.class);
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    var value = true;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.of(config));
    when(config.getBooleanValue()).thenReturn(value);

    // when
    Boolean returnedValue = globalConfigService.getBooleanValue(property);

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldReturnNullWhenBooleanPropertyDoesNotExist() {
    // given
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.empty());

    // when
    Boolean returnedValue = globalConfigService.getBooleanValue(property);

    // then
    assertNull(returnedValue);
  }

  @Test
  void shouldReturnLongListValueWhenPropertyExistsAndIsLongList() {
    // given
    var config = mock(GlobalConfig.class);
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    List<Long> value = List.of(123L, 234L, 345L);
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.of(config));
    when(config.getLongListValue()).thenReturn(value);

    // when
    List<Long> returnedValue = globalConfigService.getLongListValue(property);

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldReturnEmptyListWhenLongListPropertyDoesNotExist() {
    // given
    var property = GlobalConfigProperty.BUYER_LOGO_DIR;
    when(globalConfigRepository.findByProperty(property)).thenReturn(Optional.empty());

    // when
    List<Long> returnedValue = globalConfigService.getLongListValue(property);

    // then
    assertEquals(List.of(), returnedValue);
  }
}
