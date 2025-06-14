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

@Getter
@Setter
@JsonInclude(Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealSiteDTO {

  @ToString.Include private Long pid;

  @EqualsAndHashCode.Include @ToString.Include @NotNull private Long sitePid;

  @EqualsAndHashCode.Include @ToString.Include private String siteName;

  private final List<DealPlacementDTO> placements = new ArrayList<>();

  public void setPlacements(List<DealPlacementDTO> placements) {
    this.placements.clear();
    this.placements.addAll(placements);
  }
}
