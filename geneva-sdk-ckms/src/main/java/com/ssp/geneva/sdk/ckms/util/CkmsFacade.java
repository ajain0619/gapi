package com.ssp.geneva.sdk.ckms.util;

import com.yahoo.ykeykey.client.YKeyKeyClient;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Provides the ability to switch between the real implementation of YKeyKeyClient in prod and a
 * mock implementation for testing. If the "ckms.ykeykey.mock" property is set to true or the client
 * is null, the secrets will be read from the file at ckms/mockYKeyKey.json. Otherwise, a real
 * client will be used to fetch the secrets.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CkmsFacade {

  /**
   * Get interactor associated to given bank.
   *
   * @param client {@link YKeyKeyClient}
   * @return ckmsInteractor {@link CkmsInteractor}
   */
  public static CkmsInteractor getCkmsInteractor(YKeyKeyClient client) {
    return Objects.isNull(client) ? new YkeyClientMock() : new YkeyKeyClientWrapper(client);
  }
}
