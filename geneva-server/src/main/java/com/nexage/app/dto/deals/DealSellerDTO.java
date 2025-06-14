package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_EMPTY)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealSellerDTO {

  @ToString.Include private Long pid;

  @EqualsAndHashCode.Include @ToString.Include @NotNull private Long sellerPid;

  @EqualsAndHashCode.Include @ToString.Include private String sellerName;

  @EqualsAndHashCode.Include @ToString.Include private Long sellerSeatPid;

  private final List<DealSiteDTO> sites = new ArrayList<>();

  public void setSites(List<DealSiteDTO> sites) {
    this.sites.clear();
    this.sites.addAll(sites);
  }
}
