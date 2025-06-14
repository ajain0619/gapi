package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "iso_language")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Immutable
public class IsoLanguage implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String languageName;

  @Column(name = "iso_639_1_code")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String languageCode;
}
