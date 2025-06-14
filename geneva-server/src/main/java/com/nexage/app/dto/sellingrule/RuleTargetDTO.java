package com.nexage.app.dto.sellingrule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
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
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class RuleTargetDTO {

  @Null(groups = CreateGroup.class)
  private Long pid;

  @Null(groups = CreateGroup.class)
  private Integer version;

  @NotNull private Status status;
  @NotNull private MatchType matchType;
  @NotNull private RuleTargetType targetType;
  @NotBlank private String data;
}
