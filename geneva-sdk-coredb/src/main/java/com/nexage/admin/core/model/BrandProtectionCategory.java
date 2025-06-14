package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "brand_protection_category")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BrandProtectionCategory implements Serializable {

  private static final long serialVersionUid = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @EqualsAndHashCode.Include @ToString.Include @Column private String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
  private Collection<BrandProtectionTag> brandProtectionTags;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "archive_date")
  private Date archiveDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_date")
  private Date updateDate;
}
