package com.nexage.admin.core.model.feeadjustment;

import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.NONE)
@Entity
@Immutable
@Audited
@Table(name = "company")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FeeAdjustmentCompanyView implements Serializable {

  private static final long serialVersionUID = -8390703602023048263L;

  @ToString.Include @EqualsAndHashCode.Include @Id @Column private Long pid;

  @ToString.Include @EqualsAndHashCode.Include @Column private String name;

  @Column(name = "type", nullable = false, length = 50)
  @NotNull
  @Enumerated(EnumType.STRING)
  private CompanyType type;
}
