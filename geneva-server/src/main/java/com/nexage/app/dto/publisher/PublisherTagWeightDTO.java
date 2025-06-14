package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PublisherTagWeightDTO {

  private Double weight;
  private PublisherTagDTO tag;

  public PublisherTagWeightDTO() {}

  private PublisherTagWeightDTO(Builder builder) {
    weight = builder.weight;
    tag = builder.tag;
  }

  public Double getWeight() {
    return weight;
  }

  public PublisherTagDTO getTag() {
    return tag;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Double weight;
    private PublisherTagDTO tag;

    public Builder withWeight(Double weight) {
      this.weight = weight;
      return this;
    }

    public Builder withTag(PublisherTagDTO tag) {
      this.tag = tag;
      return this;
    }

    public PublisherTagWeightDTO build() {
      return new PublisherTagWeightDTO(this);
    }
  }
}
