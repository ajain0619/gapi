package com.nexage.app.dto.tag;

public class TagCleanupResultsDTO {
  private long tagsRemoved;
  private long tiersRemoved;

  public TagCleanupResultsDTO(long tagsRemoved, long tiersRemoved) {
    this.tagsRemoved = tagsRemoved;
    this.tiersRemoved = tiersRemoved;
  }

  public long getTagsRemoved() {
    return tagsRemoved;
  }

  public long getTiersRemoved() {
    return tiersRemoved;
  }
}
