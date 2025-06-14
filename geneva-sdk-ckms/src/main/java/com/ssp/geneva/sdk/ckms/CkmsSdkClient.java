package com.ssp.geneva.sdk.ckms;

import com.ssp.geneva.sdk.ckms.config.CkmsSdkConfigProperties;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Builder
public class CkmsSdkClient {

  @NonNull @NotNull private final CkmsSdkConfigProperties ckmsSdkConfigProperties;
  @NonNull @NotNull private final CkmsProvider ckmsProvider;
}
