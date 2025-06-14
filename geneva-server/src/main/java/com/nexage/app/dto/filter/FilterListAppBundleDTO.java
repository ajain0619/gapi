package com.nexage.app.dto.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@ToString
public class FilterListAppBundleDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Integer pid;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer filterListId;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private String app;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private MediaStatusDTO status;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @Min(groups = UpdateGroup.class, value = 0, message = ValidationMessages.WRONG_NUMBER_MIN)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer version;
}
