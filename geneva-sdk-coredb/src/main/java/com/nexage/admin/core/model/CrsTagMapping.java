package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "crs_tag_mapping")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CrsTagMapping implements Serializable {

  private static final long serialVersionUID = -1416911289737316878L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne
  @JoinColumn(name = "bprot_tag_pid", referencedColumnName = "pid")
  private BrandProtectionTag tag;

  @Column(name = "crs_tag_id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long crsTagId;

  @Column(name = "crs_tag_attribute_id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long crsTagAttributeId;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_date")
  private Date updateDate;
}
