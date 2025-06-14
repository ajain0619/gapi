package com.nexage.app.util.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.ContentRating;
import com.nexage.admin.core.repository.ContentRatingRepository;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RuleContentRatingValidation implements RuleTargetValidation {

  private final ContentRatingRepository contentRatingRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public RuleContentRatingValidation(
      ContentRatingRepository contentRatingRepository, ObjectMapper objectMapper) {
    this.contentRatingRepository = contentRatingRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean isValid(String data) {
    /** ratingData : "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 1 }" */
    final RatingData ratingData;
    try {
      ratingData = objectMapper.readValue(data, RatingData.class);
    } catch (JsonProcessingException e) {
      log.debug("Invalid Json : " + e);
      return false;
    }

    return ratingData.hasValidData() && hasRatings(ratingData);
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_RATING;
  }

  private boolean hasRatings(RatingData ratingData) {
    List<String> ratingList = ratingData.ratings;

    List<ContentRating> resultList = contentRatingRepository.findByRatingIn(ratingList);

    return (ratingList.size() == resultList.size());
  }

  private static class RatingData {
    private List<String> ratings;
    private int excludeTrafficWithoutRating;

    public RatingData(
        @JsonProperty(value = "ratings", required = true) List<String> ratings,
        @JsonProperty(value = "excludeTrafficWithoutRating", required = true)
            int excludeTrafficWithoutRating) {
      this.ratings = ratings;
      this.excludeTrafficWithoutRating = excludeTrafficWithoutRating;
    }

    public boolean hasValidData() {
      return ((excludeTrafficWithoutRating == 0 || excludeTrafficWithoutRating == 1)
          && !(ratings.isEmpty() && 0 == excludeTrafficWithoutRating));
    }
  }
}
