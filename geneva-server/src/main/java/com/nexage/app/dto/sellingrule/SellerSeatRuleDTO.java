package com.nexage.app.dto.sellingrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.Status;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
public class SellerSeatRuleDTO extends RuleDTO {

  @NotNull private Long sellerSeatPid;

  @Builder
  public SellerSeatRuleDTO(
      Long pid,
      Integer version,
      String name,
      String description,
      Status status,
      Set<IntendedActionDTO> intendedActions,
      RuleType type,
      Set<RuleTargetDTO> targets,
      Long sellerSeatPid) {
    super(pid, version, name, description, status, intendedActions, type, targets);
    this.sellerSeatPid = sellerSeatPid;
  }
}
