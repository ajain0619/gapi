package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal.DealStatus;
import com.nexage.app.dto.AssignedInventoryType;
import com.nexage.app.dto.BaseDealDTO;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DealDTO extends BaseDealDTO {

  private Integer auctionType;

  private AssignedInventoryType assignedInventoryType;

  private BigDecimal floor;

  private Boolean visibility;

  private Date updatedOn;

  private Integer version;

  private Long rulePid;

  private Long createdBy;

  private Integer dealCategory;

  @Builder
  public DealDTO(
      Long pid,
      String dealId,
      String description,
      Date start,
      Date stop,
      String currency,
      DealStatus status,
      Date creationDate,
      DealPriorityType priorityType,
      Integer auctionType,
      Integer dealCategory,
      AssignedInventoryType assignedInventoryType,
      BigDecimal floor,
      Boolean visibility,
      Date updatedOn,
      Integer version,
      Long rulePid,
      Long createdBy,
      PlacementFormulaStatus placementFormulaStatus) {
    super(
        pid,
        dealId,
        description,
        start,
        stop,
        currency,
        status,
        creationDate,
        priorityType,
        placementFormulaStatus);
    this.auctionType = auctionType;
    this.dealCategory = dealCategory;
    this.assignedInventoryType = assignedInventoryType;
    this.floor = floor;
    this.visibility = visibility;
    this.updatedOn = updatedOn;
    this.version = version;
    this.rulePid = rulePid;
    this.createdBy = createdBy;
  }
}
