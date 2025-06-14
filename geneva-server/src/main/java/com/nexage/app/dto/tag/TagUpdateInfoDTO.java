package com.nexage.app.dto.tag;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TagUpdateInfoDTO {
  private String tagName;
  private BigDecimal previousGrossFloor;
  private BigDecimal newGrossFloor;
  private BigDecimal previousLowFloor;
  private BigDecimal newLowFloor;

  public TagUpdateInfoDTO(Builder builder) {
    this.tagName = builder.tagName;
    this.previousGrossFloor = builder.previousGrossFloor;
    this.previousLowFloor = builder.previousLowFloor;
    this.newLowFloor = builder.newLowFloor;
    this.newGrossFloor = builder.newGrossFloor;
  }

  public String getTagName() {
    return tagName;
  }

  public BigDecimal getPreviousGrossFloor() {
    return previousGrossFloor;
  }

  public BigDecimal getNewGrossFloor() {
    return newGrossFloor;
  }

  public BigDecimal getPreviousLowFloor() {
    return previousLowFloor;
  }

  public BigDecimal getNewLowFloor() {
    return newLowFloor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((newGrossFloor == null) ? 0 : newGrossFloor.hashCode());
    result = prime * result + ((newLowFloor == null) ? 0 : newLowFloor.hashCode());
    result = prime * result + ((previousGrossFloor == null) ? 0 : previousGrossFloor.hashCode());
    result = prime * result + ((previousLowFloor == null) ? 0 : previousLowFloor.hashCode());
    result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    TagUpdateInfoDTO other = (TagUpdateInfoDTO) obj;
    if (newGrossFloor == null) {
      if (other.newGrossFloor != null) return false;
    } else if (!newGrossFloor.equals(other.newGrossFloor)) return false;
    if (newLowFloor == null) {
      if (other.newLowFloor != null) return false;
    } else if (!newLowFloor.equals(other.newLowFloor)) return false;
    if (previousGrossFloor == null) {
      if (other.previousGrossFloor != null) return false;
    } else if (!previousGrossFloor.equals(other.previousGrossFloor)) return false;
    if (previousLowFloor == null) {
      if (other.previousLowFloor != null) return false;
    } else if (!previousLowFloor.equals(other.previousLowFloor)) return false;
    if (tagName == null) {
      if (other.tagName != null) return false;
    } else if (!tagName.equals(other.tagName)) return false;
    return true;
  }

  public static final class Builder {

    private String tagName;
    private BigDecimal previousGrossFloor;
    private BigDecimal newGrossFloor;
    private BigDecimal previousLowFloor;
    private BigDecimal newLowFloor;

    public Builder setTagName(String tagName) {
      this.tagName = tagName;
      return this;
    }

    public Builder setPreviousGrossFloor(BigDecimal previousGrossFloor) {
      this.previousGrossFloor = previousGrossFloor;
      return this;
    }

    public Builder setNewGrossFloor(BigDecimal newGrossFloor) {
      this.newGrossFloor = newGrossFloor;
      return this;
    }

    public Builder setPreviousLowFloor(BigDecimal previousLowFloor) {
      this.previousLowFloor = previousLowFloor;
      return this;
    }

    public Builder setNewLowFloor(BigDecimal newLowFloor) {
      this.newLowFloor = newLowFloor;
      return this;
    }

    public TagUpdateInfoDTO build() {
      return new TagUpdateInfoDTO(this);
    }
  }
}
