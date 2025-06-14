package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class PublisherDefaultRTBProfileAssignmentsDTO {

  private Set<PublisherSiteDTO> sites;
  private Set<PublisherPositionDTO> positions;
  private Boolean isPublisherDefault;

  private PublisherDefaultRTBProfileAssignmentsDTO(Builder builder) {
    this.sites = builder.sites;
    this.positions = builder.positions;
    this.isPublisherDefault = builder.isPublisherDefault;
  }

  @JsonProperty("sites")
  public Set<PublisherSiteDTO> getRTBProfileSites() {
    return sites;
  }

  public void setRTBProfileSites(Set<PublisherSiteDTO> sites) {
    this.sites = sites;
  }

  @JsonProperty("positions")
  public Set<PublisherPositionDTO> getRTBProfilePositions() {
    return positions;
  }

  public void setRTBProfilePositions(Set<PublisherPositionDTO> positions) {
    this.positions = positions;
  }

  public Boolean getIsPublisherDefault() {
    return isPublisherDefault;
  }

  public void setIsPublisherDefault(Boolean isPublisherDefault) {
    this.isPublisherDefault = isPublisherDefault;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Set<PublisherSiteDTO> sites;
    private Set<PublisherPositionDTO> positions;
    private Boolean isPublisherDefault;

    public Builder withSitesForRTBProfile(Set<PublisherSiteDTO> sites) {
      this.sites = sites;
      return this;
    }

    public Builder withPositionsForRTBProfile(Set<PublisherPositionDTO> positions) {
      this.positions = positions;
      return this;
    }

    public Builder withIsPublisherDefault(Boolean isPublisherDefault) {
      this.isPublisherDefault = isPublisherDefault;
      return this;
    }

    public PublisherDefaultRTBProfileAssignmentsDTO build() {
      return new PublisherDefaultRTBProfileAssignmentsDTO(this);
    }
  }
}
