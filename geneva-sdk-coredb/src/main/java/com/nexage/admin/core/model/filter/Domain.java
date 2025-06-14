package com.nexage.admin.core.model.filter;

import com.nexage.admin.core.enums.ApprovalStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "domain")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Domain implements Serializable {

  private static final long serialVersionUID = 4559772877393019086L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "domain")
  private String domain;

  @ToString.Include
  @Column(name = "approval_status", insertable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private ApprovalStatus approvalStatus;

  @Version private Integer version;
}
