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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

/** The persistent class for the identity_provider database table. */
@Entity
@Audited
@Table(name = "identity_provider")
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Data
public class IdentityProviderView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;
}
