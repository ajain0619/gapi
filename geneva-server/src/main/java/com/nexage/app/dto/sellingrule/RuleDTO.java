package com.nexage.app.dto.sellingrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.RuleTargetConstraint;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public abstract class RuleDTO {

  @Null(groups = CreateGroup.class)
  @NotNull(groups = UpdateGroup.class)
  private Long pid;

  @Null(groups = CreateGroup.class)
  @NotNull(groups = UpdateGroup.class)
  private Integer version;

  @NotNull
  @Size(max = 255)
  private String name;

  @Size(max = 1000)
  private String description;

  @NotNull private Status status;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @Valid
  @Size(min = 1, max = 1)
  private Set<IntendedActionDTO> intendedActions = new HashSet<>();

  @NotNull private RuleType type;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @Valid
  private Set<
          @RuleTargetConstraint(groups = {Default.class, CreateGroup.class, UpdateGroup.class})
          RuleTargetDTO>
      targets = new HashSet<>();
}
