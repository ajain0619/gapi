package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.custom.SealedObjectConverter;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.util.CipherUtil;
import java.io.Serializable;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name = "seller_adsource")
@Log4j2
@Getter
@Setter
public class SellerAdSource implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  private Long pid;

  @Column(name = "seller_pid")
  private Long sellerPid;

  @ManyToOne
  @JoinColumn(
      name = "seller_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  @JsonIgnore
  @JsonBackReference
  @NotAudited
  private Company seller;

  @Column(name = "adsource_pid")
  private Long adSourcePid;

  @ManyToOne
  @JoinColumn(
      name = "adsource_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  @JsonIgnore
  @JsonBackReference
  @NotAudited
  private AdSource adSource;

  @Column(name = "adnetreport_username")
  private String username;

  @Column(name = "adnetreport_password")
  @Convert(converter = SealedObjectConverter.class)
  private SealedObject password;

  @Column(name = "adnetreport_apitoken")
  private String apiToken;

  @Column(name = "adnetreport_apikey")
  private String apiKey;

  @Version private int version;

  public String getPassword() {
    try {
      return (String) password.getObject(CipherUtil.getCipher(Cipher.DECRYPT_MODE));
    } catch (Exception ex) {
      log.error("Failed to get password", ex);
      return null;
    }
  }

  public void setPassword(String password) {
    try {
      this.password = new SealedObject(password, CipherUtil.getCipher(Cipher.ENCRYPT_MODE));
    } catch (Exception ex) {
      log.error("Failed to set password", ex);
    }
  }
}
