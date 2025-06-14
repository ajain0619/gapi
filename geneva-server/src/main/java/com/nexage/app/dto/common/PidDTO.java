package com.nexage.app.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PidDTO implements Serializable {

  private Long pid;

  public PidDTO() {}

  public PidDTO(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }
}
