package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoView;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "position")
@Immutable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleFormulaPositionView implements Serializable {

  private static final long serialVersionUID = -5517544758564770230L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @ToString.Include
  private RuleFormulaSiteView site;

  @Column(name = "site_pid", insertable = false, updatable = false)
  private Long sitePid;

  @ToString.Include @Column private String name;

  @Column(name = "placement_type")
  @Enumerated(EnumType.ORDINAL)
  @ToString.Include
  private PlacementCategory type;

  @ToString.Include @Column private String memo;

  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @ToString.Include
  private Status status;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pid", referencedColumnName = "pid")
  @ToString.Include
  private PlacementVideoView placementVideoView;

  @ToString.Include @Column private Integer height;
  @ToString.Include @Column private Integer width;

  @ManyToMany
  @JoinTable(
      name = "position_attributes",
      joinColumns = @JoinColumn(name = "position_pid"),
      inverseJoinColumns = @JoinColumn(name = "attribute_values_pid"))
  @ToString.Include
  private Set<RuleFormulaAttributeValueView> attributeValues;
}
