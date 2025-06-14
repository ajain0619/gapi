package com.nexage.app.dto.common;

public class IdentityDTO {
  public IdentityDTO(String id) {
    this.id = id;
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
