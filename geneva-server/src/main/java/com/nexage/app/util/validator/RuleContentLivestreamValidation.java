package com.nexage.app.util.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.ContentLivestream;
import com.nexage.admin.core.enums.RuleTargetType;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class RuleContentLivestreamValidation implements RuleTargetValidation {

  private final ObjectMapper objectMapper;

  @Override
  public boolean isValid(String data) {
    /**
     * livestreamData : "{\"livestream\": [\"LIVE\" ], \"excludeTrafficWithoutLivestream\" : 1 }"
     */
    final LivestreamData livestreamData;
    try {
      livestreamData = objectMapper.readValue(data, LivestreamData.class);
    } catch (JsonProcessingException e) {
      log.debug("Invalid Json : " + e);
      return false;
    }
    return livestreamData.hasValidData() && hasLivestream(livestreamData);
  }

  private boolean hasLivestream(LivestreamData livestreamData) {
    List<String> livestreams = livestreamData.livestream;
    List<ContentLivestream> livestreamEnumList = Arrays.asList(ContentLivestream.values());

    return livestreamEnumList.stream()
            .filter(contentLivestream -> livestreams.contains(contentLivestream.toString()))
            .count()
        == livestreams.size();
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_LIVESTREAM;
  }

  private static class LivestreamData {
    private List<String> livestream;
    private int excludeTrafficWithoutLivestream;

    public LivestreamData(
        @JsonProperty(value = "livestream", required = true) List<String> livestream,
        @JsonProperty(value = "excludeTrafficWithoutLivestream", required = true)
            int excludeTrafficWithoutLivestream) {
      this.livestream = livestream;
      this.excludeTrafficWithoutLivestream = excludeTrafficWithoutLivestream;
    }

    public boolean hasValidData() {
      return ((excludeTrafficWithoutLivestream == 0 || excludeTrafficWithoutLivestream == 1)
          && !(livestream.isEmpty() && 0 == excludeTrafficWithoutLivestream));
    }
  }
}
