package com.nexage.app.dto.deals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealBidderDTO {
  private Long pid;
  private Long companyPid;
  private String name;
}
