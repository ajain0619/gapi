package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class PublisherDefaultRTBProfileDTO extends PublisherRTBProfileDTO {

  private Long defaultRtbProfileOwnerCompanyPid;
  private PublisherTagDTO tag;
  private PublisherDefaultRTBProfileAssignmentsDTO assignments;
  private Long numberOfEffectivePlacements;

  public Long getDefaultRtbProfileOwnerCompanyPid() {
    return defaultRtbProfileOwnerCompanyPid;
  }

  public void setDefaultRtbProfileOwnerCompanyPid(Long defaultRtbProfileOwnerCompanyPid) {
    this.defaultRtbProfileOwnerCompanyPid = defaultRtbProfileOwnerCompanyPid;
  }

  public void setNumberOfEffectivePlacements(Long numberOfEffectivePlacements) {
    this.numberOfEffectivePlacements = numberOfEffectivePlacements;
  }

  public Long getNumberOfEffectivePlacements() {
    return numberOfEffectivePlacements;
  }

  public PublisherTagDTO getTag() {
    return tag;
  }

  public void setTag(PublisherTagDTO tag) {
    this.tag = tag;
  }

  public PublisherDefaultRTBProfileAssignmentsDTO getAssignments() {
    return assignments;
  }

  public void setAssignments(PublisherDefaultRTBProfileAssignmentsDTO assignments) {
    this.assignments = assignments;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder
      extends PublisherRTBProfileDTO.Builder<PublisherDefaultRTBProfileDTO, Builder> {

    public Builder() {
      this.profile = new PublisherDefaultRTBProfileDTO();
      this.builder = this;
    }

    public Builder withDefaultRtbProfileOwnerCompanyPid(Long companyPid) {
      profile.defaultRtbProfileOwnerCompanyPid = companyPid;
      return builder;
    }

    public Builder withTag(PublisherTagDTO tag) {
      profile.tag = tag;
      return builder;
    }

    public Builder withRtbProfileAssignments(PublisherDefaultRTBProfileAssignmentsDTO assignments) {
      profile.assignments = assignments;
      return builder;
    }

    public Builder withNumberOfEffectivePlacements(Long numberOfEffectivePlacements) {
      profile.numberOfEffectivePlacements = numberOfEffectivePlacements;
      return builder;
    }

    public PublisherDefaultRTBProfileDTO build() {
      return profile;
    }
  }
}
