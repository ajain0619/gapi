package com.nexage.app.dto.buyer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.model.BuyerSeat;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerSeatDTO {

  private Long pid;
  @NotNull private String name;
  @NotNull private String seat;
  @NotNull private boolean enabled;
  @NotNull private Long buyerGroupPid;
  private Integer version;
  private Long companyPid;
  @NotNull private boolean buyerTransparencyFeedEnabled;
  private Long buyerTransparencyDataFeedPid;

  public BuyerSeatDTO(BuyerSeat buyerSeat) {
    this.pid = buyerSeat.getPid();
    this.name = buyerSeat.getName();
    this.seat = buyerSeat.getSeat();
    this.enabled = buyerSeat.isEnabled();
    this.buyerGroupPid =
        buyerSeat.getBuyerGroup() != null ? buyerSeat.getBuyerGroup().getPid() : null;
    this.version = buyerSeat.getVersion();
    this.companyPid = buyerSeat.getCompany().getPid();
    this.buyerTransparencyFeedEnabled = buyerSeat.getBuyerTransparencyFeedEnabled();
    this.buyerTransparencyDataFeedPid = buyerSeat.getBuyerTransparencyDataFeedPid();
  }
}
