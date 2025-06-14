package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
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

@Entity
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "rtb_profile_library_association")
public class RTBProfileLibraryAssociation implements Serializable {

  private static final long serialVersionUID = -3616987436203959733L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne
  @JoinColumn(name = "rtb_profile_pid")
  @JsonBackReference
  @EqualsAndHashCode.Include
  private RTBProfile rtbprofile;

  @ManyToOne
  @JoinColumn(name = "library_pid")
  @JsonBackReference
  @EqualsAndHashCode.Include
  private RtbProfileLibrary library;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;
}
