package com.nexage.app.util.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.ContentGenreRepository;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RuleContentGenreValidation implements RuleTargetValidation {

  private final ContentGenreRepository contentGenreRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public RuleContentGenreValidation(
      ContentGenreRepository contentGenreRepository, ObjectMapper objectMapper) {
    this.contentGenreRepository = contentGenreRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean isValid(String data) {
    /** genreData : "{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 1 }" */
    final GenreData genreData;
    try {
      genreData = objectMapper.readValue(data, GenreData.class);
    } catch (JsonProcessingException e) {
      log.debug("Invalid Json : " + e);
      return false;
    }

    return genreData.hasValidData() && hasGenres(genreData);
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_GENRE;
  }

  private boolean hasGenres(GenreData genreData) {
    List<String> genres = genreData.genres;

    return (genres.size() == contentGenreRepository.existsByGenre(genres));
  }

  private static class GenreData {
    private List<String> genres;
    private int excludeTrafficWithoutGenre;

    public GenreData(
        @JsonProperty(value = "genres", required = true) List<String> genres,
        @JsonProperty(value = "excludeTrafficWithoutGenre", required = true)
            int excludeTrafficWithoutGenre) {
      this.genres = genres;
      this.excludeTrafficWithoutGenre = excludeTrafficWithoutGenre;
    }

    public boolean hasValidData() {
      return ((excludeTrafficWithoutGenre == 0 || excludeTrafficWithoutGenre == 1)
          && !(genres.isEmpty() && 0 == excludeTrafficWithoutGenre));
    }
  }
}
