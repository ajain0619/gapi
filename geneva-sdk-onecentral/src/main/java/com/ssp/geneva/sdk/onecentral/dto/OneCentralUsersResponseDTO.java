package com.ssp.geneva.sdk.onecentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class OneCentralUsersResponseDTO {
  @JsonProperty("list")
  private List<OneCentralUserResponseDTO.OneCentralUser> users;

  private Integer totalCount;
}
