package com.nexage.app.dto.user;

import com.ssp.geneva.common.model.inventory.CompanyType;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CompanyViewDTO {

  @NotNull private Long pid;

  private String name;

  private CompanyType type;

  private boolean adStrictApproval;
}
