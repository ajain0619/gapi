package com.nexage.app.dto.buyer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class BuyerRegionLimitDTO {

  private Long pid;
  private Integer version;
  private String name;

  private Map<String, String> countries;
  private Integer maximumQps;

  private BuyerRegionLimitDTO() {}

  private BuyerRegionLimitDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.name = builder.name;
    this.maximumQps = builder.maximumQps;
    this.countries = builder.countries;
  }

  public Long getPid() {
    return pid;
  }

  public Integer getVersion() {
    return version;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getCountries() {
    return countries;
  }

  public Integer getMaximumQps() {
    return maximumQps;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private String name;
    private Map<String, String> countries;
    private Integer maximumQps;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withMaximumQps(Integer maximumQps) {
      this.maximumQps = maximumQps;
      return this;
    }

    public Builder withCountry(String country) {
      if (countries == null) {
        countries = new HashMap<>();
      }
      countries.put(country, country);
      return this;
    }

    public BuyerRegionLimitDTO build() {
      return new BuyerRegionLimitDTO(this);
    }
  }
}
