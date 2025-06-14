package com.nexage.app.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TagPerformanceMetricsDTO.Builder.class)
public class TagPerformanceMetricsDTO {

  private final long tagPid;
  private final String tagName;
  private final long requests;
  private final BigDecimal revenue;

  private final Long served;
  private final Long delivered;
  private final Long clicks;

  private TagPerformanceMetricsDTO(Builder builder) {
    this.tagPid = builder.tagPid;
    this.tagName = builder.tagName;
    this.requests = builder.requests;
    this.revenue = builder.revenue;

    this.served = builder.served;
    this.delivered = builder.delivered;
    this.clicks = builder.clicks;
  }

  public long getTagPid() {
    return tagPid;
  }

  public String getTagName() {
    return tagName;
  }

  public long getRequests() {
    return requests;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public Long getServed() {
    return served;
  }

  public Long getDelivered() {
    return delivered;
  }

  public Long getClicks() {
    return clicks;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clicks == null) ? 0 : clicks.hashCode());
    result = prime * result + ((delivered == null) ? 0 : delivered.hashCode());
    result = prime * result + (int) (requests ^ (requests >>> 32));
    result = prime * result + ((served == null) ? 0 : served.hashCode());
    result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
    result = prime * result + (int) (tagPid ^ (tagPid >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TagPerformanceMetricsDTO other = (TagPerformanceMetricsDTO) obj;
    if (clicks == null) {
      if (other.clicks != null) return false;
    } else if (!clicks.equals(other.clicks)) return false;
    if (delivered == null) {
      if (other.delivered != null) return false;
    } else if (!delivered.equals(other.delivered)) return false;
    if (requests != other.requests) return false;
    if (served == null) {
      if (other.served != null) return false;
    } else if (!served.equals(other.served)) return false;
    if (tagName == null) {
      if (other.tagName != null) return false;
    } else if (!tagName.equals(other.tagName)) return false;
    if (tagPid != other.tagPid) return false;
    return true;
  }

  public static final class Builder {

    private long tagPid;
    private String tagName;
    private long requests;
    private BigDecimal revenue;

    private Long served;
    private Long delivered;
    private Long clicks;

    public Builder withDefaults(long tagPid, String tagName, long requests, BigDecimal revenue) {
      this.tagPid = tagPid;
      this.requests = requests;
      this.tagName = tagName;
      this.revenue = revenue;
      return this;
    }

    public Builder withServed(long served) {
      this.served = served;
      return this;
    }

    public Builder withDelivered(long delivered) {
      this.delivered = delivered;
      return this;
    }

    public Builder withClicks(long clicks) {
      this.clicks = clicks;
      return this;
    }

    public TagPerformanceMetricsDTO build() {
      return new TagPerformanceMetricsDTO(this);
    }
  }
}
