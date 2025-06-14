package com.ssp.geneva.sdk.ckms.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class YkeyClientMock implements CkmsInteractor {

  public static final String MOCK_DATA_LOC = "/ckms/mockYKeyKey.json";
  protected static final Gson GSON = new Gson();

  /** {@inheritDoc} */
  @Override
  public String getSecret(final String keyName, final String keyGroupName) {
    log.info("Mock YKeyKeyClient enabled; fetching key {}", keyName);
    Objects.requireNonNull(getClass().getResourceAsStream(MOCK_DATA_LOC), "File Path non-existent");

    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(MOCK_DATA_LOC)))) {
      Type entryType = new TypeToken<ArrayList<CredentialMockEntry>>() {}.getType();
      List<CredentialMockEntry> entries = GSON.fromJson(in, entryType);

      for (CredentialMockEntry entry : entries) {
        if (Objects.equals(entry.id, keyName)) {
          return entry.secretKey;
        }
      }
    } catch (IOException e) {
      log.error("Unable to load mock YKeyKey configuration", e);
      return StringUtils.EMPTY;
    }

    return StringUtils.EMPTY;
  }

  /** {@inheritDoc} */
  @Override
  public void refresh() {
    // nothing to do in the mocked impl
  }

  /** {@inheritDoc} */
  @Override
  public void loadKeyGroup(String keyGroup) {
    // nothing to do in the mocked impl
  }

  protected static class CredentialMockEntry {

    private String id;
    private String secretKey;

    public void setId(String id) {
      this.id = id;
    }

    public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
    }

    public String toString() {
      return GSON.toJson(this);
    }
  }
}
