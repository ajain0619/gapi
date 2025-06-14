package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.ApprovalStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

/** Parent class for AppAlias and AppBundle Data's shared fields */
@MappedSuperclass
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BaseAppBundleAndAlias {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  private Date createdOn;

  @ToString.Include
  @Column(name = "approval_status", insertable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private ApprovalStatus approvalStatus;

  @PrePersist
  public void prePersist() {
    Date nowDate = new Date();
    createdOn = nowDate;
    updatedOn = nowDate;
  }

  @PreUpdate
  public void preUpdate() {
    updatedOn = new Date();
  }
}
