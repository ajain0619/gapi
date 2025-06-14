package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.SDKHandshakeConfiguration;
import com.nexage.admin.core.repository.SdkHandshakeConfigRepository;
import com.nexage.app.dto.SDKHandshakeConfigDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.assemblers.SDKHandshakeAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SdkHandshakeServiceImplTest {

  @Mock private SDKHandshakeAssembler assembler;
  @Mock private SdkHandshakeConfigRepository sdkConfigRepository;
  @InjectMocks private SdkHandshakeServiceImpl sdkHandshakeService;

  @Test
  void shouldGetAllKeys() {
    // given
    List<SDKHandshakeConfiguration> configurations =
        List.of(
            createHandshakeConfig(1L, "key1", "value1"),
            createHandshakeConfig(2L, "key2", "value2"));
    when(sdkConfigRepository.findAll()).thenReturn(configurations);
    when(assembler.makeWithOnlyKeys(any()))
        .thenAnswer(i -> makeWithOnlyKeys(i.getArgument(0, SDKHandshakeConfiguration.class)));

    // when
    List<SDKHandshakeConfigDTO> returnedConfigs = sdkHandshakeService.getAllKeys();

    // then
    List<String> keys =
        returnedConfigs.stream()
            .map(SDKHandshakeConfigDTO::getHandshakeKey)
            .collect(Collectors.toList());
    assertEquals(List.of("key1", "key2"), keys);
  }

  @Test
  void shouldFindById() {
    // given
    var pid = 1L;
    SDKHandshakeConfiguration config = createHandshakeConfig(pid, "", "");
    SDKHandshakeConfigDTO dto = makeWithOnlyKeys(config);
    when(sdkConfigRepository.findById(pid)).thenReturn(Optional.of(config));
    when(assembler.make(config)).thenReturn(dto);

    // when
    SDKHandshakeConfigDTO returnedDto = sdkHandshakeService.findById(pid);

    // then
    assertEquals(dto, returnedDto);
  }

  @Test
  void shouldDelete() {
    // given
    var pid = 1L;

    // when
    sdkHandshakeService.delete(pid);

    // then
    verify(sdkConfigRepository).deleteById(pid);
  }

  @Test
  void shouldReturnTrueThatHandshakeKeyExistsIfItExists() {
    // given
    var key = "key";
    when(sdkConfigRepository.existsByHandshakeKey(key)).thenReturn(true);

    // when
    boolean result = sdkHandshakeService.handshakeKeyExists(key);

    // then
    assertTrue(result);
  }

  @Test
  void shouldUpdateConfigIfItExistsAndVersionMatches() {
    // given
    var pid = 1L;
    SDKHandshakeConfiguration config = createHandshakeConfig(pid, "key", "value");
    config.setVersion(1);
    SDKHandshakeConfigDTO updateDto = SDKHandshakeConfigDTO.newBuilder().withVersion(1).build();
    SDKHandshakeConfiguration updatedConfig = createHandshakeConfig(pid, "key", "updatedValue");
    var updatedConfigDto = new SDKHandshakeConfigDTO();
    when(sdkConfigRepository.findById(pid)).thenReturn(Optional.of(config));
    when(assembler.apply(config, updateDto)).thenReturn(updatedConfig);
    when(sdkConfigRepository.saveAndFlush(updatedConfig)).thenReturn(updatedConfig);
    when(assembler.make(updatedConfig)).thenReturn(updatedConfigDto);

    // when
    SDKHandshakeConfigDTO returnedDto = sdkHandshakeService.updateConfig(pid, updateDto);

    // then
    assertEquals(updatedConfigDto, returnedDto);
  }

  @Test
  void shouldThrowExceptionOnUpdatedConfigVersionMismatch() {
    // given
    var pid = 1L;
    SDKHandshakeConfiguration config = createHandshakeConfig(pid, "key", "value");
    config.setVersion(1);
    SDKHandshakeConfigDTO updateDto = SDKHandshakeConfigDTO.newBuilder().withVersion(2).build();
    when(sdkConfigRepository.findById(pid)).thenReturn(Optional.of(config));

    // throws exception when
    assertThrows(StaleStateException.class, () -> sdkHandshakeService.updateConfig(pid, updateDto));
  }

  @Test
  void shouldThrowExceptionWhenUpdatedConfigDoesNotExist() {
    // given
    var pid = 1L;
    var updateDto = new SDKHandshakeConfigDTO();
    when(sdkConfigRepository.findById(pid)).thenReturn(Optional.empty());

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sdkHandshakeService.updateConfig(pid, updateDto));

    // then
    assertEquals(ServerErrorCodes.SERVER_HANDSHAKE_CONFIG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldCreateConfig() {
    // given
    var createDto = new SDKHandshakeConfigDTO();
    var createdConfig = new SDKHandshakeConfiguration();
    var createdDto = new SDKHandshakeConfigDTO();
    when(assembler.apply(any(), eq(createDto))).thenReturn(createdConfig);
    when(sdkConfigRepository.saveAndFlush(createdConfig)).thenReturn(createdConfig);
    when(assembler.make(createdConfig)).thenReturn(createdDto);

    // when
    SDKHandshakeConfigDTO returnedDto = sdkHandshakeService.createConfig(createDto);

    // then
    assertEquals(createdDto, returnedDto);
  }

  private SDKHandshakeConfiguration createHandshakeConfig(Long pid, String key, String value) {
    var config = new SDKHandshakeConfiguration();
    config.setPid(pid);
    config.setHandshakeKey(key);
    config.setHandshakeValue(value);
    return config;
  }

  private SDKHandshakeConfigDTO makeWithOnlyKeys(SDKHandshakeConfiguration configuration) {
    return SDKHandshakeConfigDTO.newBuilder()
        .withPid(configuration.getPid())
        .withHandshakeKey(configuration.getHandshakeKey())
        .build();
  }
}
