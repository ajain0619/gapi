package com.ssp.geneva.sdk.identityb2b;

import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfigProperties;
import com.ssp.geneva.sdk.identityb2b.repository.AccessTokenRepository;
import com.ssp.geneva.sdk.identityb2b.repository.UserAuthenticationRepository;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Builder
public class IdentityB2bSdkClient {

  @NonNull @NotNull private final IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties;
  @NonNull @NotNull private final AccessTokenRepository accessTokenRepository;
  @NonNull @NotNull private final UserAuthenticationRepository userAuthenticationRepository;
}
