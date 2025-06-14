package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "rtb_profile_library")
public class RtbProfileLibrary implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "name", nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  private Integer version;

  @Column(name = "privilege_level", nullable = false, length = 30)
  @NotNull
  @Enumerated(EnumType.STRING)
  RTBProfileLibraryPrivilegeLevel privilegeLevel;

  @Column(name = "publisher_pid")
  @EqualsAndHashCode.Include
  private Long publisherPid;

  @Column(name = "is_default_eligible", nullable = false)
  @JsonProperty("isDefaultEligible")
  private boolean isDefaultEligible;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "rtb_profile_library_item",
      joinColumns = {@JoinColumn(name = "library_pid", referencedColumnName = "pid")},
      inverseJoinColumns = {@JoinColumn(name = "item_pid", referencedColumnName = "pid")})
  @AuditJoinTable(
      name = "rtb_profile_library_item_aud",
      inverseJoinColumns = {@JoinColumn(name = "item_pid", referencedColumnName = "pid")})
  private Set<RtbProfileGroup> groups = new HashSet<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "library" /*, cascade=CascadeType.ALL, orphanRemoval=true*/)
  @JsonIgnore
  private Set<RTBProfileLibraryAssociation> profileLibraryAssociations = Sets.newHashSet();

  public void setGroups(Set<RtbProfileGroup> groups) {
    this.groups.clear();
    this.groups.addAll(groups);
  }
}
