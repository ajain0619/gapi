package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DirectDeal;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Table(name = "deal_rule")
@Audited
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealRule implements Serializable {

  private static final long serialVersionUID = -2939166387016685313L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @Column(name = "rule_group_pid")
  private Long rulePid;
}
