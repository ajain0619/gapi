package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "deal")
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Getter
@Setter
public class DirectDealView implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long pid;

  @Column(name = "description")
  private String description;

  @Column(name = "id")
  private String id;
}
