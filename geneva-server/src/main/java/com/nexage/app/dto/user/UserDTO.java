package com.nexage.app.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.util.validator.IdentityIqUserConstraint;
import com.nexage.app.util.validator.UserCreateConstraint;
import com.nexage.app.util.validator.UserUpdateConstraint;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UserCreateConstraint(groups = CreateGroup.class)
@UserUpdateConstraint(groups = UpdateGroup.class)
@IdentityIqUserConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

  private String id;

  private Long pid;

  private Integer version;

  @NotNull
  @Size(max = 100)
  private String userName;

  @Size(max = 100)
  private String oneCentralUserName;

  @Size(max = 100)
  private String name;

  @Size(max = 100)
  @NotNull(groups = CreateGroup.class)
  private String firstName;

  @Size(max = 100)
  @NotNull(groups = CreateGroup.class)
  private String lastName;

  @NotNull @Email private String email;

  @Size(max = 20)
  private String contactNumber;

  @Size(max = 100)
  private String title;

  @NotNull private User.Role role;

  @NotNull private boolean enabled;

  @NotNull private boolean global;

  private boolean primaryContact;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String contactName;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Email
  private String contactEmail;

  private Set<@Valid CompanyViewDTO> companies;

  @JsonIgnoreProperties("sellers")
  private SellerSeatDTO sellerSeat;

  private boolean dealAdmin;
}
