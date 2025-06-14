package com.ssp.geneva.sdk.xandr;

import com.ssp.geneva.sdk.xandr.config.XandrSdkConfigProperties;
import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
@Builder
public class XandrSdkClient {
  @NonNull @NotNull private final XandrSdkConfigProperties xandrSdkConfigProperties;
  @NonNull @NotNull private final RestTemplate xandrRestTemplate;
  @NonNull @NotNull private final DealRepository dealRepository;
  @NonNull @NotNull private final AuthRepository authRepository;
}
