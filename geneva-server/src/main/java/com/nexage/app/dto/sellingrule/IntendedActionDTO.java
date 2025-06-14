package com.nexage.app.dto.sellingrule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@JsonInclude(NON_NULL)
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class IntendedActionDTO {

  @Null(groups = CreateGroup.class)
  private final Long pid;

  @Null(groups = CreateGroup.class)
  private final Integer version;

  @NotNull private final RuleActionType actionType;
  @NotBlank private final String actionData;
}
