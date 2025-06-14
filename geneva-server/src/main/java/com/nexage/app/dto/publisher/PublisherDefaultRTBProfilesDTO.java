package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class PublisherDefaultRTBProfilesDTO {

  private Long publisherPid;
  private List<PublisherDefaultRTBProfileDTO> defaultRtbProfiles;

  private PublisherDefaultRTBProfilesDTO(Builder builder) {
    this.defaultRtbProfiles = builder.defaultRtbProfiles;
    this.publisherPid = builder.publisherPid;
  }

  public List<PublisherDefaultRTBProfileDTO> getDefaultRtbProfiles() {
    return defaultRtbProfiles;
  }

  public void setDefaultRtbProfiles(List<PublisherDefaultRTBProfileDTO> defaultrtbProfiles) {
    this.defaultRtbProfiles = defaultrtbProfiles;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public void setPublisherPid(Long publisherPid) {
    this.publisherPid = publisherPid;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private List<PublisherDefaultRTBProfileDTO> defaultRtbProfiles;
    private Long publisherPid;

    public Builder withDefaultRtbProfiles(List<PublisherDefaultRTBProfileDTO> defaultRtbProfiles) {
      this.defaultRtbProfiles = defaultRtbProfiles;
      return this;
    }

    public Builder withPublisherPid(Long publisherPid) {
      this.publisherPid = publisherPid;
      return this;
    }

    public PublisherDefaultRTBProfilesDTO build() {
      return new PublisherDefaultRTBProfilesDTO(this);
    }
  }
}
