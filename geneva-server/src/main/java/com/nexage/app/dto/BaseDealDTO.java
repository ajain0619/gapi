package com.nexage.app.dto;

import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDealDTO {

  @Schema(title = "Primary key for the table")
  private Long pid;

  @Schema(title = "Unique id string for the deal")
  private String dealId;

  @Schema(title = "Deal description")
  private String description;

  @Schema(title = "Deal start date")
  private Date start;

  @Schema(title = "Deal Stop date")
  private Date stop;

  @Default private String currency = "USD";

  private DirectDeal.DealStatus status;

  private Date creationDate;

  @Setter private DealPriorityType priorityType;

  @Schema(title = "Match By formula status")
  @Setter
  private PlacementFormulaStatus placementFormulaStatus;
}
