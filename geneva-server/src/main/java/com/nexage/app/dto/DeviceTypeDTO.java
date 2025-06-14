package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTypeDTO implements Serializable {
  private Long pid;

  @JsonProperty("deviceTypeId")
  private Integer id;

  private String name;
}
