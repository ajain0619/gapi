package com.ssp.geneva.common.security.oauth2.oidc;

import com.nexage.admin.core.util.CipherUtil;
import org.apache.commons.lang3.StringUtils;

public interface OpenIdCredentialsConfigHandler {

  String CLIENT_ID_KEY = "geneva.sso.oidc.client.id";
  String CLIENT_SECRET_KEY = "geneva.sso.oidc.client.secret";

  String getClientId();

  String getClientSecret();

  default String decrypt(String encryptedValue) {
    if (StringUtils.isNotBlank(encryptedValue)) {
      return CipherUtil.decrypt(encryptedValue);
    }
    return "";
  }
}
