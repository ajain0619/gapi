/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nexage.admin.core.model.Tag;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

/** @author Gamal Dawood <gamal.dawood@teamaol.com> */
@Table(name = "tag_controller")
@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Audited
public class TagController implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "auto_expand")
  private Boolean autoExpand;

  @Version
  @Column(name = "VERSION")
  private Integer version;

  @OneToOne
  @JoinColumn(name = "tag_pid", referencedColumnName = "pid")
  @JsonBackReference
  private Tag tag;

  public TagController(Long pid) {
    this.pid = pid;
  }

  public TagController(Long pid, boolean autoExpand) {
    this.pid = pid;
    this.autoExpand = autoExpand;
  }
}
