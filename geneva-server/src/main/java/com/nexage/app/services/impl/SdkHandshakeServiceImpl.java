package com.nexage.app.services.impl;

import com.nexage.admin.core.model.SDKHandshakeConfiguration;
import com.nexage.admin.core.repository.SdkHandshakeConfigRepository;
import com.nexage.app.dto.SDKHandshakeConfigDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SdkHandshakeService;
import com.nexage.app.util.assemblers.SDKHandshakeAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sdkHandshakeService")
@Transactional
public class SdkHandshakeServiceImpl implements SdkHandshakeService {

  private final SDKHandshakeAssembler assembler;
  private final SdkHandshakeConfigRepository sdkConfigRepository;

  @Autowired
  public SdkHandshakeServiceImpl(
      SDKHandshakeAssembler assembler, SdkHandshakeConfigRepository sdkConfigRepository) {
    this.assembler = assembler;
    this.sdkConfigRepository = sdkConfigRepository;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<SDKHandshakeConfigDTO> getAllKeys() {
    List<SDKHandshakeConfigDTO> result = null;
    List<SDKHandshakeConfiguration> dbObjects = sdkConfigRepository.findAll();
    if (!dbObjects.isEmpty()) {
      result = new ArrayList<>();
      for (SDKHandshakeConfiguration eachDbObject : dbObjects) {
        result.add(assembler.makeWithOnlyKeys(eachDbObject));
      }
    }
    return result;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public SDKHandshakeConfigDTO findById(long pid) {
    return sdkConfigRepository.findById(pid).map(assembler::make).orElse(null);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void delete(long pid) {
    sdkConfigRepository.deleteById(pid);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public boolean handshakeKeyExists(String handshakeKey) {
    return sdkConfigRepository.existsByHandshakeKey(handshakeKey);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public SDKHandshakeConfigDTO updateConfig(long pid, SDKHandshakeConfigDTO config) {
    SDKHandshakeConfiguration dbObject =
        sdkConfigRepository
            .findById(pid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_HANDSHAKE_CONFIG_NOT_FOUND));
    if (!dbObject.getVersion().equals(config.getVersion())) {
      throw new StaleStateException("SDKHandshakeConfig has a different version of the data");
    }
    SDKHandshakeConfiguration assembledDbObject = assembler.apply(dbObject, config);
    SDKHandshakeConfiguration updatedConfig = sdkConfigRepository.saveAndFlush(assembledDbObject);
    return assembler.make(updatedConfig);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public SDKHandshakeConfigDTO createConfig(SDKHandshakeConfigDTO config) {
    SDKHandshakeConfiguration assembledDbObject =
        assembler.apply(new SDKHandshakeConfiguration(), config);
    SDKHandshakeConfiguration createdConfig = sdkConfigRepository.saveAndFlush(assembledDbObject);
    return assembler.make(createdConfig);
  }
}
