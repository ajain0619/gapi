package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPasswordRepositoryRequest implements Serializable {

  long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @NonNull @NotNull private String username;
  @EqualsAndHashCode.Include @NonNull @NotNull private String oldPassword;
  @EqualsAndHashCode.Include @NonNull @NotNull private String newPassword;
}
