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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "brand_protection_tag")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BrandProtectionTag implements Serializable {

  private static final long serialVersionUid = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne
  @JoinColumn(name = "parent_tag_pid")
  private BrandProtectionTag parentTag;

  @ManyToOne
  @JoinColumn(name = "category_pid", referencedColumnName = "pid")
  private BrandProtectionCategory category;

  @EqualsAndHashCode.Include @ToString.Include @Column private String name;

  @Column(name = "rtb_id")
  private String rtbId;

  @Column(name = "free_text_tag")
  private Boolean freeTextTag;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_date")
  private Date updateDate;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
  @LazyCollection(LazyCollectionOption.FALSE)
  private Collection<CrsTagMapping> crsTagMappings;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
  private Collection<BrandProtectionTagValues> tagValues;
}
