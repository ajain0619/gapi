package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Audited
@Table(name = "rtb_profile_library_item")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RtbProfileLibraryItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne
  @JoinColumn(name = "library_pid")
  @JsonBackReference
  private RtbProfileLibrary library;

  @ManyToOne
  @JoinColumn(name = "item_pid")
  private RtbProfileGroup group;

  @Version
  @Column(name = "VERSION", nullable = false)
  protected Integer version;
}
