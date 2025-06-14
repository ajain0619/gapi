package com.nexage.app.util.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RuleContentChannelValidation implements RuleTargetValidation {

  private static class ChannelData {
    private List<String> channels;
    private int excludeTrafficWithoutChannel;

    public ChannelData(
        @JsonProperty(value = "channels", required = true) List<String> channels,
        @JsonProperty(value = "excludeTrafficWithoutChannel", required = true)
            int excludeTrafficWithoutChannel) {
      this.channels = channels;
      this.excludeTrafficWithoutChannel = excludeTrafficWithoutChannel;
    }

    public boolean hasValidData() {

      if (channels.isEmpty() && 0 == excludeTrafficWithoutChannel) return false;

      for (String channel : channels) {
        if (MAX_SIZE_OF_CHANNEL < channel.length() || channel.contains(",")) return false;
      }
      return (0 == excludeTrafficWithoutChannel || 1 == excludeTrafficWithoutChannel);
    }
  }

  private static final int MAX_SIZE_OF_CHANNEL = 32;
  private final ObjectMapper objectMapper;

  @Autowired
  public RuleContentChannelValidation(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean isValid(String targetData) {

    /* Example valid json
    "data": "{\"channels\": [ \"CNN\" ,\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutChannel\" : 1/0 }" */

    ChannelData channelData;
    try {
      channelData = objectMapper.readValue(targetData, ChannelData.class);
    } catch (Exception e) {
      log.error("Content Channel is not a valid JSON");
      return false;
    }

    return channelData.hasValidData();
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_CHANNEL;
  }
}
