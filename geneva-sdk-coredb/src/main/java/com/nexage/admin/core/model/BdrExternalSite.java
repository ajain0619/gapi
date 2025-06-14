package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** The persistent class for the bdr_site_info database table. */
@Table(name = "bdr_site_info")
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BdrExternalSite implements Serializable {

  private static final long serialVersionUID = 234L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "exchange_ext_id")
  @ToString.Include
  private long exchangeExtId;

  @Column(name = "site_alias")
  @ToString.Include
  private String siteAlias;

  @Column(name = "site_name_alias")
  @ToString.Include
  private String siteNameAlias;

  @Column(name = "type")
  @ToString.Include
  private String siteType;

  @Column(name = "app_bundle")
  @ToString.Include
  private String appBundle;

  @Column(name = "iab_categories")
  @ToString.Include
  private String iabCategories;

  @Column(name = "bdr_pub_info_pid")
  @ToString.Include
  private long bdrPubInfoPid;

  @ToString.Include private Boolean isInTarget;

  @ToString.Include private String pubNameAlias;
}
