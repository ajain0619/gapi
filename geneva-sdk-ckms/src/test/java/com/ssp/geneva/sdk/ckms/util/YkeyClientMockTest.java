package com.ssp.geneva.sdk.ckms.util;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YkeyClientMockTest {

  CkmsInteractor ykeyClientMock = new YkeyClientMock();

  @Test
  @DisplayName("Should get secret with real name and return secret")
  void shouldGetSecretWithRealNameAndReturnSecret() {

    // given && when
    String secret = ykeyClientMock.getSecret("ID_1", "");

    // then
    Assertions.assertEquals("KEY_1", secret);
  }

  @Test
  @DisplayName("Should get secret with missing name and return blank")
  void shouldGetSecretWithMissingNameAndReturnBlank() {

    // given && when
    String secret = ykeyClientMock.getSecret("ID_2", "");

    // then
    Assertions.assertEquals("", secret);
  }

  @Test
  @DisplayName("Should load key group and confirm calling")
  void shouldLoadKeyGroupAndConfirmCalling() {

    // given
    ykeyClientMock = spy(ykeyClientMock);

    // when
    ykeyClientMock.loadKeyGroup("random_group");

    // then
    Mockito.verify(ykeyClientMock, times(1)).loadKeyGroup("random_group");
  }

  @Test
  @DisplayName("Should call credential entry to string return json string")
  void shouldCallCredentialEntryToStringReturnJsonString() {

    // given
    YkeyClientMock.CredentialMockEntry credentialMockEntry =
        new YkeyClientMock.CredentialMockEntry();
    credentialMockEntry.setId("dummy_id");
    credentialMockEntry.setSecretKey("dummy_secret");

    // when
    String res = credentialMockEntry.toString();

    // then
    Assertions.assertEquals("""
        {"id":"dummy_id","secretKey":"dummy_secret"}""", res);
  }
}
