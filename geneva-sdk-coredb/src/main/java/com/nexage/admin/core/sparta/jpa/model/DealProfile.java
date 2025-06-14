package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DirectDeal;
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
import org.hibernate.envers.RelationTargetAuditMode;

@Table(name = "deal_profile")
@Audited
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealProfile implements Serializable {

  private static final long serialVersionUID = 676263990570762460L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private long pid;

  @ManyToOne
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Version
  @Column(name = "VERSION", nullable = false)
  private int version;

  @ManyToOne
  @JoinColumn(name = "profile_tag_pid", referencedColumnName = "pid")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private DealRtbProfileViewUsingFormulas rtbProfile;
}
