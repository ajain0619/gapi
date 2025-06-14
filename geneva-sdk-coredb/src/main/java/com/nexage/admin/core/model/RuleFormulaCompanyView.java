package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "company")
@Immutable
public class RuleFormulaCompanyView implements Serializable {

  private static final long serialVersionUID = -7666560115844085600L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column @EqualsAndHashCode.Include @ToString.Include private String name;

  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Status status;

  @Column(name = "default_rtb_profiles_enabled", updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean defaultRtbProfilesEnabled;

  @ManyToMany
  @JoinTable(
      name = "company_attributes",
      joinColumns = @JoinColumn(name = "company_pid"),
      inverseJoinColumns = @JoinColumn(name = "attribute_values_pid"))
  private Set<RuleFormulaAttributeValueView> attributeValues;
}
