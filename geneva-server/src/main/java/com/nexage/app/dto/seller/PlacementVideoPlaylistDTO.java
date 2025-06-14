package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.MediaType;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@ToString
public class PlacementVideoPlaylistDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Long pid;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer version;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(
      groups = {UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private Long placementVideoPid;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private String fallbackURL;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private MediaType mediaType;
}
