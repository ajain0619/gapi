package com.nexage.app.util.assemblers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.SDKHandshakeConfiguration;
import com.nexage.app.dto.SDKHandshakeConfigDTO;
import java.io.IOException;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SDKHandshakeAssembler extends NoContextAssembler {

  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "version", "handshakeKey", "handshakeValue");

  private final ObjectMapper objectMapper;
  private static final String CONFIG_NAME = "config";
  private static final String CONFIG_VALUE = "${HASH_VALUE}";

  @Autowired
  public SDKHandshakeAssembler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public SDKHandshakeConfigDTO make(SDKHandshakeConfiguration dbSDKConfig) {
    return make(dbSDKConfig, DEFAULT_FIELDS);
  }

  public SDKHandshakeConfigDTO makeWithOnlyKeys(SDKHandshakeConfiguration dbSDKConfig) {
    return make(dbSDKConfig, Sets.newHashSet("pid", "handshakeKey"));
  }

  public SDKHandshakeConfigDTO make(SDKHandshakeConfiguration dbSDKConfig, Set<String> fields) {
    SDKHandshakeConfigDTO.Builder handshakeConfig = SDKHandshakeConfigDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          handshakeConfig.withPid(dbSDKConfig.getPid());
          break;
        case "version":
          handshakeConfig.withVersion(dbSDKConfig.getVersion());
          break;
        case "handshakeKey":
          if (dbSDKConfig.getHandshakeKey() != null) {
            handshakeConfig.withHandshakeKey(dbSDKConfig.getHandshakeKey());
          }
          break;
        case "handshakeValue":
          if (dbSDKConfig.getHandshakeValue() != null) {
            String handshakeVal = dbSDKConfig.getHandshakeValue();
            handshakeConfig.withHandshakeValue(convertHandshakeValueToJSON(handshakeVal));
          }
          break;
        default:
      }
    }
    return handshakeConfig.build();
  }

  public SDKHandshakeConfiguration apply(
      SDKHandshakeConfiguration persistentObject, SDKHandshakeConfigDTO config) {
    ObjectNode objectNode = null;
    if (config != null && config.getHandshakeValue() != null) {
      objectNode = (ObjectNode) config.getHandshakeValue();
      objectNode.put(CONFIG_NAME, CONFIG_VALUE);
      String handshakeValue = convertHandshakeJSONToString(config.getHandshakeValue());
      persistentObject.setHandshakeKey(config.getHandshakeKey());
      persistentObject.setHandshakeValue(handshakeValue);
    }
    return persistentObject;
  }

  private JsonNode convertHandshakeValueToJSON(String handshakeValue) {
    JsonNode result = null;
    if (handshakeValue != null) {
      try {
        JsonNode json = objectMapper.readTree(handshakeValue);
        if (json != null) {
          ObjectNode objectNode = (ObjectNode) json;
          objectNode.remove(CONFIG_NAME);
          result = objectNode;
        }
      } catch (IOException e) {
        log.warn(
            "Unable to convert handshakeValue to JSON from the database String : {}",
            handshakeValue);
      }
    }
    return result;
  }

  private String convertHandshakeJSONToString(JsonNode handshakeValue) {
    String result = null;
    if (handshakeValue != null && !handshakeValue.isNull()) {
      result = handshakeValue.toString();
    }
    return result;
  }
}
