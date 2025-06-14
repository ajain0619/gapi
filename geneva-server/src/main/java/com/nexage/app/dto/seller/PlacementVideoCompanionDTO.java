package com.nexage.app.dto.seller;

import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlacementVideoCompanionDTO {
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Long pid;

  @Min(groups = UpdateGroup.class, value = 0, message = ValidationMessages.WRONG_NUMBER_MIN)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Integer version;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer height;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer width;
}
