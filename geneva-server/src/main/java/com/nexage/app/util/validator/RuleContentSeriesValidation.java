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
public class RuleContentSeriesValidation implements RuleTargetValidation {

  private static final int MAX_SIZE_OF_SERIES = 32;
  private final ObjectMapper objectMapper;

  @Autowired
  public RuleContentSeriesValidation(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private static class SeriesData {
    private List<String> series;
    private int excludeTrafficWithoutSeries;

    public SeriesData(
        @JsonProperty(value = "series", required = true) List<String> series,
        @JsonProperty(value = "excludeTrafficWithoutSeries", required = true)
            int excludeTrafficWithoutSeries) {
      this.series = series;
      this.excludeTrafficWithoutSeries = excludeTrafficWithoutSeries;
    }

    public boolean hasValidData() {

      if (series.isEmpty() && 0 == excludeTrafficWithoutSeries) return false;

      for (String series_name : series) {
        if (MAX_SIZE_OF_SERIES < series_name.length() || series_name.contains(",")) return false;
      }
      return (0 == excludeTrafficWithoutSeries || 1 == excludeTrafficWithoutSeries);
    }
  }

  @Override
  public boolean isValid(String targetData) {

    /* Example valid json
    "data": "{\"series\": [ \"The family\" ,\"Baby’s day #2\" ,\"War@Cry\" ], \"excludeTrafficWithoutSeries\" : 1/0 }" */

    final SeriesData seriesData;
    try {
      seriesData = objectMapper.readValue(targetData, SeriesData.class);
    } catch (Exception e) {
      log.error("Content Series is not a valid JSON");
      return false;
    }

    return seriesData.hasValidData();
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_SERIES;
  }
}
