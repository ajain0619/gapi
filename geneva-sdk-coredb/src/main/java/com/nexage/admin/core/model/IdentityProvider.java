package com.nexage.admin.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

/** The persistent class for the identity_provider database table. */
@Audited
@Table(name = "identity_provider")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class IdentityProvider {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Integer version;

  @Column(name = "name")
  @Size(max = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  @NotNull
  private String name;

  @Column(name = "display_name")
  @Size(max = 100)
  @NotNull
  private String displayName;

  @Column(name = "provider_id")
  @NotNull
  private Integer providerId;

  @Column(name = "domain")
  @Size(max = 200)
  @NotNull
  private String domain;

  @Column(name = "enabled")
  @NotNull
  private Boolean enabled = false;

  @Column(name = "ui_visible")
  @NotNull
  private Boolean uiVisible = false;
}
