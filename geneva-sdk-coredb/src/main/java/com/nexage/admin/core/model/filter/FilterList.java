package com.nexage.admin.core.model.filter;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deny_allow_filter_list")
@Data
@Audited
@Where(clause = "status=1")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilterList implements Serializable {

  private static final long serialVersionUID = -7886153436992258504L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer pid;

  @Column(name = "company_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long companyId;

  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "upload_status")
  private FilterListUploadStatus uploadStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private FilterListType type;

  @Column(name = "invalid")
  private Integer invalid;

  @Column(name = "duplicate")
  private Integer duplicate;

  @Column(name = "error")
  private Integer error;

  @Column(name = "total")
  private Integer total;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @Column(name = "updated_on", insertable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date updated;

  @Column(name = "status", insertable = false)
  private Boolean active;

  @Version private Integer version;
}
