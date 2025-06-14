package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Immutable
@Audited
@Table(name = "position")
@Getter
@Setter
@NoArgsConstructor
public class RuleDeployedPosition implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id private Long pid;

  @Column private String memo;

  @Column(nullable = false)
  private String name;

  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @ManyToOne
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @NotNull
  private RuleDeployedSite site;
}
