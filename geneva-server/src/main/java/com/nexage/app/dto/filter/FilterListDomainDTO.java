package com.nexage.app.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterListDomainDTO {
  private Integer pid;
  private Integer filterListId;
  private String domain;
  private MediaStatusDTO status;
  private Integer version;
}
