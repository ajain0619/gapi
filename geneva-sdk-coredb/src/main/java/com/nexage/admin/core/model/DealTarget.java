package com.nexage.admin.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
@Audited
@DiscriminatorValue("DEAL")
public class DealTarget extends BaseTarget {

  private static final long serialVersionUID = 2997656068471160009L;

  @ManyToOne
  @JoinColumn(name = "item_pid", referencedColumnName = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private DirectDeal deal;
}
