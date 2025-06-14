package com.nexage.app.dto.buyer;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyerGroupDTO {

  private Long pid;
  @NotNull private String name;
  @NotNull private String sfdcLineId;
  @NotNull private String sfdcIoId;
  @NotNull private String currency;
  @NotNull private String billingCountry;
  @NotNull private Boolean billable;
  private Integer version;
  private Long companyPid;
}
