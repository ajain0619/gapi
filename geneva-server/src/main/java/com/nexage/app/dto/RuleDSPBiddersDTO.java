package com.nexage.app.dto;

import com.nexage.app.dto.deals.DealBidderDTO;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleDSPBiddersDTO {

  private Long pid;
  private String name;
  private Set<DealBidderDTO> bidders;
}
