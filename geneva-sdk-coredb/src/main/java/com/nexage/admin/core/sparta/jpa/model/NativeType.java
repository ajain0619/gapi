package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Table(name = "native_type")
@Immutable
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class NativeType implements Serializable {

  private static final long serialVersionUID = 8299491154966500146L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "type", nullable = false)
  private String type;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "nativeType",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonBackReference
  private List<NativeTypeAdsource> nativeTypeAdSource = new ArrayList<>();
}
