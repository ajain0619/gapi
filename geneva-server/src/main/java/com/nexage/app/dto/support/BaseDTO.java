package com.nexage.app.dto.support;

import java.io.Serializable;

/**
 * Base dto object
 *
 * @author Eugeny Yurko
 * @since 10.09.2014
 */
public class BaseDTO implements Serializable {

  private long pid;
  private String name;

  @SuppressWarnings("unused")
  public BaseDTO() {}

  protected BaseDTO(Builder builder) {
    pid = builder.pid;
    name = builder.name;
  }

  public long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private long pid;
    private String name;

    protected Builder() {}

    public Builder withPid(long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public BaseDTO build() {
      return new BaseDTO(this);
    }
  }
}
