package com.ssp.geneva.sdk.onecentral;

import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.repository.AuthorizationManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserAuthorizationRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementPasswordRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserMigrationRepository;
import com.ssp.geneva.sdk.onecentral.service.OneCentralUserService;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Builder
public class OneCentralSdkClient {

  @NonNull @NotNull private final OneCentralSdkConfigProperties oneCentralSdkConfigProperties;
  @NonNull @NotNull private final UserMigrationRepository userMigrationRepository;
  @NonNull @NotNull private final UserManagementRepository userManagementRepository;
  @NonNull @NotNull private final UserManagementPasswordRepository userManagementPasswordRepository;
  @NonNull @NotNull private final UserAuthorizationRepository userAuthorizationRepository;
  @NonNull @NotNull private final OneCentralUserService userService;

  @NonNull @NotNull
  private final AuthorizationManagementRepository authorizationManagementRepository;
}
