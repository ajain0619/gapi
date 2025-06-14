package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexage.admin.core.util.CipherUtil;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import java.io.Serializable;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@JsonInclude(Include.NON_NULL)
@Log4j2
@Getter
@NoArgsConstructor
public class PublisherAdSourceDefaultsDTO implements Serializable {

  private static final long serialVersionUID = -6082665117491149003L;

  @NotNull(groups = {UpdateGroup.class})
  @Null(groups = {CreateGroup.class})
  private Long pid;

  private Integer version;
  private Long sellerPid;
  private Long adSourcePid;
  private String username;
  private SealedObject password;
  private String apiToken;
  private String apiKey;

  private PublisherAdSourceDefaultsDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.sellerPid = builder.sellerPid;
    this.adSourcePid = builder.adSourcePid;
    this.username = builder.username;
    this.password = builder.password;
    this.apiToken = builder.apiToken;
    this.apiKey = builder.apiKey;
  }

  @JsonProperty("password")
  public String getPassword() {
    try {
      return password != null
          ? (String) password.getObject(CipherUtil.getCipher(Cipher.DECRYPT_MODE))
          : null;
    } catch (Exception ex) {
      log.error("Failed to get password: ", ex);
      return null;
    }
  }

  public void setPassword(String password) {
    try {
      this.password = new SealedObject(password, CipherUtil.getCipher(Cipher.ENCRYPT_MODE));
    } catch (Exception ex) {
      log.error("Failed to set password: ", ex);
    }
  }

  public String getApiToken() {
    return apiToken;
  }

  public String getApiKey() {
    return apiKey;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((adSourcePid == null) ? 0 : adSourcePid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PublisherAdSourceDefaultsDTO other = (PublisherAdSourceDefaultsDTO) obj;
    if (adSourcePid == null) {
      if (other.adSourcePid != null) return false;
    } else if (!adSourcePid.equals(other.adSourcePid)) return false;
    return true;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private Long sellerPid;
    private Long adSourcePid;
    private String username;
    private SealedObject password;
    private String apiToken;
    private String apiKey;

    public Builder withPid(Long pid, Set<String> fields) {
      this.pid = fields.contains("pid") ? pid : null;
      return this;
    }

    public Builder withVersion(Integer version, Set<String> fields) {
      this.version = fields.contains("version") ? version : null;
      return this;
    }

    public Builder withSellerPid(Long sellerPid, Set<String> fields) {
      this.sellerPid = fields.contains("sellerPid") ? sellerPid : null;
      return this;
    }

    public Builder withAdSourcePid(Long adsourcePid, Set<String> fields) {
      this.adSourcePid = fields.contains("adSourcePid") ? adsourcePid : null;
      return this;
    }

    public Builder withUserName(String username, Set<String> fields) {
      this.username = fields.contains("username") ? username : null;
      return this;
    }

    public Builder withPassword(String password, Set<String> fields) {
      try {
        this.password =
            fields.contains("password")
                ? new SealedObject(password, CipherUtil.getCipher(Cipher.ENCRYPT_MODE))
                : null;
      } catch (Exception ex) {
        log.error("Failed to set password", ex);
      }
      return this;
    }

    public Builder withApiToken(String apiToken, Set<String> fields) {
      this.apiToken = fields.contains("apiToken") ? apiToken : null;
      return this;
    }

    public Builder withApiKey(String apiKey, Set<String> fields) {
      this.apiKey = fields.contains("apiKey") ? apiKey : null;
      return this;
    }

    public PublisherAdSourceDefaultsDTO build() {
      return new PublisherAdSourceDefaultsDTO(this);
    }
  }
}
