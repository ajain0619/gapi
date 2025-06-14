package com.nexage.app.services;

import com.nexage.app.dto.SDKHandshakeConfigDTO;
import java.util.List;

public interface SdkHandshakeService {

  List<SDKHandshakeConfigDTO> getAllKeys();

  SDKHandshakeConfigDTO findById(long pid);

  void delete(long pid);

  boolean handshakeKeyExists(String handshakeKey);

  SDKHandshakeConfigDTO updateConfig(long pid, SDKHandshakeConfigDTO config);

  SDKHandshakeConfigDTO createConfig(SDKHandshakeConfigDTO config);
}
