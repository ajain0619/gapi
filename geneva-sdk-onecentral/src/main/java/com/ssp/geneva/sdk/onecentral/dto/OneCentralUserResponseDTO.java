package com.ssp.geneva.sdk.onecentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.ToString;

public class OneCentralUserResponseDTO {

  @JsonInclude(Include.NON_NULL)
  @Getter
  @ToString
  public static class OneCentralUser {
    private Integer id;
    private String firstName;
    private String lastName;
    private @ToString.Include String username;
    private @ToString.Include String email;
  }
}
