package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class SDKHandshakeConfigDTO {

  private Long pid;

  private String handshakeKey;

  private JsonNode handshakeValue;

  private Integer version;

  private SDKHandshakeConfigDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.handshakeKey = builder.handshakeKey;
    this.handshakeValue = builder.handshakeValue;
  }

  public Long getPid() {
    return pid;
  }

  public String getHandshakeKey() {
    return handshakeKey;
  }

  public JsonNode getHandshakeValue() {
    return handshakeValue;
  }

  public Integer getVersion() {
    return version;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private String handshakeKey;
    private JsonNode handshakeValue;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withHandshakeKey(String handshakeKey) {
      this.handshakeKey = handshakeKey;
      return this;
    }

    public Builder withHandshakeValue(JsonNode handshakeValue) {
      this.handshakeValue = handshakeValue;
      return this;
    }

    public SDKHandshakeConfigDTO build() {
      return new SDKHandshakeConfigDTO(this);
    }
  }
}
