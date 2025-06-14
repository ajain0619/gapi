package com.nexage.app.dto.inventory.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class InventoryAttributeValueDTO {

  private Long pid;
  private String value;
  private boolean isEnabled;
  private Integer version;
}
