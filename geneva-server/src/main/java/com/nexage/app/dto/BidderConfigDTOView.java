package com.nexage.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BidderConfigDTOView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(title = "Primary key for the table")
  @NonNull
  @EqualsAndHashCode.Include
  private Long pid;

  @Schema(title = "Unique id")
  @NonNull
  @EqualsAndHashCode.Include
  private String id;

  @Schema(title = "Unique id string for the Buyer")
  @NonNull
  @EqualsAndHashCode.Include
  private Long companyPid;

  @Schema(title = "Name")
  private String name;

  @Schema(title = "Integration Protocol")
  private String formatType;

  @Schema(title = "Billing Source")
  private String billingSource;

  @Schema(title = "Bid Currency")
  private String defaultBidCurrency;

  @Schema(title = "Version")
  @NonNull
  private Integer version;
}
