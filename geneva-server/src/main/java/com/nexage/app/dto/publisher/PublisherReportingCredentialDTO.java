package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class PublisherReportingCredentialDTO {

  private Long pid;
  private Integer version;
  private String name;
  private String id;
  private String accessKey;
  private String secretKey;

  private PublisherReportingCredentialDTO() {}

  private PublisherReportingCredentialDTO(Builder builder) {
    this.pid = builder.pid;
    this.id = builder.id;
    this.version = builder.version;
    this.name = builder.name;
    this.accessKey = builder.accessKey;
    this.secretKey = builder.secretKey;
  }

  public final Long getPid() {
    return pid;
  }

  public final Integer getVersion() {
    return version;
  }

  public final String getName() {
    return name;
  }

  public final String getId() {
    return id;
  }

  @JsonIgnore
  public final String getSecretKey() {
    return secretKey;
  }

  public final String getAccessKey() {
    return accessKey;
  }

  public static final Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private String id;
    private Integer version;
    private String name;
    private String accessKey;
    private String secretKey;

    private Builder() {}

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

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    @JsonProperty
    public Builder withSecretKey(String secret) {
      this.secretKey = secret;
      return this;
    }

    public Builder withAccessKey(String access) {
      this.accessKey = access;
      return this;
    }

    public PublisherReportingCredentialDTO build() {
      return new PublisherReportingCredentialDTO(this);
    }
  }
}
