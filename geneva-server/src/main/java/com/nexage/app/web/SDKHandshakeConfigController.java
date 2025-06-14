package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nexage.app.dto.SDKHandshakeConfigDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SdkHandshakeService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@Tag(name = "/sdkhandshake")
@RestController
@RequestMapping(value = "/sdkhandshake")
public class SDKHandshakeConfigController {

  private static final String DEFAULT_KEY = "*";
  private static final String PIPE_DELIMITER = "|";
  private static final String CARET_DELIMITER = "^";

  private final SdkHandshakeService sdkHandshakeService;

  public SDKHandshakeConfigController(SdkHandshakeService sdkHandshakeService) {
    this.sdkHandshakeService = sdkHandshakeService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<List<SDKHandshakeConfigDTO>> getAllHandshakeConfigKeys() {
    List<SDKHandshakeConfigDTO> allKeys = sdkHandshakeService.getAllKeys();
    if (allKeys == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(allKeys, HttpStatus.OK);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{id}")
  public ResponseEntity<SDKHandshakeConfigDTO> getHandshakeConfig(
      @PathVariable(value = "id") long id) {
    SDKHandshakeConfigDTO config = sdkHandshakeService.findById(id);
    if (config == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(config, HttpStatus.OK);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<SDKHandshakeConfigDTO> deleteHandshakeConfig(
      @PathVariable(value = "id") long id) {
    SDKHandshakeConfigDTO dbConfig = sdkHandshakeService.findById(id);
    if (dbConfig == null) {
      log.error("No sdk handshakeConfig found for id : {}", id);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_CONFIG_NOT_FOUND);
    }
    String handshakeKey = dbConfig.getHandshakeKey();
    if (handshakeKey != null && "*".equals(handshakeKey)) {
      log.error("Cannot delete the default key for SDK handshake");
      throw new GenevaAppRuntimeException(
          ServerErrorCodes.SERVER_DEFAULT_HANDSHAKEKEY_DELETE_NOT_ALLOWED);
    } else {
      sdkHandshakeService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SDKHandshakeConfigDTO> updateHandshakeConfig(
      @PathVariable(value = "id") long id, @RequestBody SDKHandshakeConfigDTO config) {
    SDKHandshakeConfigDTO dbConfig = sdkHandshakeService.findById(id);
    if (dbConfig == null) {
      log.error("No sdk handshakeConfig found for id : {}", id);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_CONFIG_NOT_FOUND);
    }
    validateDataForUpdate(id, config);
    String persistentHandshakeKey = dbConfig.getHandshakeKey();
    String dtoHandshakeKey = config.getHandshakeKey();

    if (!persistentHandshakeKey.equals(dtoHandshakeKey)) {
      if (persistentHandshakeKey.equals(DEFAULT_KEY)) {
        log.error("Default handshake Key cannot be updated. Update not allowed");
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_HANDSHAKE_DEFAULT_KEY_UPDATE_NOT_ALLOWED);
      }
      if (handshakeKeyExists(dtoHandshakeKey)) {
        log.error(
            "HandshakeKey is being updated to a new value and the key already exists. Update not allowed");
        return new ResponseEntity<>(HttpStatus.CONFLICT);
      }
    }
    SDKHandshakeConfigDTO updated = sdkHandshakeService.updateConfig(id, config);
    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SDKHandshakeConfigDTO> createHandshakeConfig(
      @RequestBody JsonNode handshakeValue,
      @RequestParam(value = "appId", required = true) String appId,
      @RequestParam(value = "keyType", required = true) KeyType keyType,
      @RequestParam(value = "key", required = false, defaultValue = DEFAULT_KEY) String key) {

    validateDataForInsert(handshakeValue, appId, key);
    String handshakeKey = getKey(appId, key, keyType);
    if (handshakeKeyExists(handshakeKey)) {
      log.error("Handshake Key already exists in the database {}", handshakeKey);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_EXISTS);
    }
    SDKHandshakeConfigDTO.Builder newConfigBuilder =
        SDKHandshakeConfigDTO.newBuilder()
            .withHandshakeKey(handshakeKey)
            .withHandshakeValue(handshakeValue);
    SDKHandshakeConfigDTO newConfigWithKey = newConfigBuilder.build();

    SDKHandshakeConfigDTO createdConfig = sdkHandshakeService.createConfig(newConfigWithKey);
    return new ResponseEntity<>(createdConfig, HttpStatus.OK);
  }

  public enum KeyType {
    SDK,
    DCN,
  }

  private boolean handshakeKeyExists(String handshakeKey) {
    return sdkHandshakeService.handshakeKeyExists(handshakeKey);
  }

  private static void validateDataForUpdate(long id, SDKHandshakeConfigDTO config) {
    if (config == null) {
      log.error("Data for update cannot be null");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_INVALID_DATA);
    }
    if (config.getPid() == null) {
      log.error("HandshakeData Pid cannot be null or empty");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_INVALID_DATA);
    }
    if (!config.getPid().equals(id)) {
      log.error("Handshake config data's pid and 'id' in the request URL don't match");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
    if (config.getVersion() == null) {
      log.error("HandshakeData Version cannot be null or empty");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_INVALID_DATA);
    }
    validateHandshakeValue(config.getHandshakeValue());
    validateHandshakeKey(config.getHandshakeKey());
  }

  private static void validateDataForInsert(JsonNode handshakeValue, String appId, String key) {
    if (StringUtils.isBlank(appId)) {
      log.error("Handshake appId cannot be null");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_APPID_REQUIRED);
    }
    validateHandshakeValue(handshakeValue);
  }

  private static void validateHandshakeKey(String handshakeKey) {
    if (StringUtils.isBlank(handshakeKey)) {
      log.error("HandshakeKey cannot be null or empty");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_INVALID_DATA);
    }
    if (DEFAULT_KEY.equals(handshakeKey)) {
      return;
    }
    if (handshakeKey.contains(PIPE_DELIMITER) && handshakeKey.contains(CARET_DELIMITER)) {
      log.error(
          "HandshakeKey (when not a default key) should have one of {} OR {}",
          PIPE_DELIMITER,
          CARET_DELIMITER);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
    }
    if (!handshakeKey.contains(PIPE_DELIMITER) && !handshakeKey.contains(CARET_DELIMITER)) {
      log.error(
          "HandshakeKey (when not a default key) should have one of {} OR {}",
          PIPE_DELIMITER,
          CARET_DELIMITER);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
    }
    if (handshakeKey.contains(PIPE_DELIMITER)) {
      String[] values = handshakeKey.split(Pattern.quote("|"));
      if (values == null || values.length != 2) {
        log.error("HandshakeKey with a {} should be of the format 'sdkVer|appId' ", PIPE_DELIMITER);
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
      }
      for (String value : values) {
        if (StringUtils.isBlank(value)) {
          log.error(
              "HandshakeKey with a {} should be of the format 'sdkVer|appId' with non blank values for sdkVer and appId",
              PIPE_DELIMITER);
          throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
        }
      }
    }

    if (handshakeKey.contains(CARET_DELIMITER)) {
      String[] values = handshakeKey.split(Pattern.quote("^"));
      if (values == null || values.length != 2) {
        log.error("HandshakeKey with a {} should be of the format 'dcn^appId' ", CARET_DELIMITER);
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
      }
      for (String value : values) {
        if (StringUtils.isBlank(value)) {
          log.error(
              "HandshakeKey with a {} should be of the format 'dcn^appId' with non blank values for dcn and appId",
              CARET_DELIMITER);
          throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_KEY_INVALID_FORMAT);
        }
      }
    }
  }

  private static void validateHandshakeValue(JsonNode handshakeValue) {
    if (handshakeValue == null || handshakeValue.isNull()) {
      log.error("HandshakeValue cannot be null or empty");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_VALUE_INVALID_FORMAT);
    }
    try {
      @SuppressWarnings("unused")
      // This is a way to check that the data under handshakeValue is in a valid JSON format
      ObjectNode validJSON = (ObjectNode) handshakeValue;
    } catch (java.lang.ClassCastException e) {
      log.error("HandshakeValue is not a valid JSON");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HANDSHAKE_VALUE_INVALID_FORMAT);
    }
  }

  private static String getKey(String appId, String key, KeyType keyType) {
    switch (keyType) {
      case SDK:
        return getKeyWithSDKVersion(appId, key);
      case DCN:
        return getKeyWithDCN(appId, key);
      default:
        return "";
    }
  }

  private static String getKeyWithSDKVersion(String appId, String sdkVersion) {
    StringBuffer sb = new StringBuffer();
    String sdkVersionKey = StringUtils.isNotBlank(sdkVersion) ? sdkVersion : DEFAULT_KEY;
    sb.append(sdkVersionKey).append(PIPE_DELIMITER).append(appId);
    return sb.toString();
  }

  private static String getKeyWithDCN(String appId, String dcn) {
    StringBuffer sb = new StringBuffer();
    String dcnKey = StringUtils.isNotBlank(dcn) ? dcn : DEFAULT_KEY;
    sb.append(dcnKey).append(CARET_DELIMITER).append(appId);
    return sb.toString();
  }
}
