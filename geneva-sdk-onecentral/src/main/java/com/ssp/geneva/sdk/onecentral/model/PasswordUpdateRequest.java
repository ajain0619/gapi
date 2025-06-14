package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PasswordUpdateRequest implements Serializable {

  long serialVersionUID = 1L;

  @EqualsAndHashCode.Include @NonNull @NotNull private String username;
  private String firstName;
  @EqualsAndHashCode.Include @NonNull @NotNull @ToString.Exclude private String oldPassword;
  @EqualsAndHashCode.Include @NonNull @NotNull @ToString.Exclude private String newPassword;
  private String systemName;
  @Email private String supportEmail;
}
