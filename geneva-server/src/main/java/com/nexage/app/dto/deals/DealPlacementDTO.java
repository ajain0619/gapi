package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealPlacementDTO {

  @ToString.Include private Long pid;

  @EqualsAndHashCode.Include @ToString.Include @NotNull private Long placementPid;

  @EqualsAndHashCode.Include @ToString.Include private String placementName;

  @EqualsAndHashCode.Include @ToString.Include private String placementMemo;
}
