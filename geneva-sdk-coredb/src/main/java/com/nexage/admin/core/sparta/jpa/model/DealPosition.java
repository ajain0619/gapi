package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DirectDeal;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deal_position")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealPosition implements Serializable {

  private static final long serialVersionUID = -5832319977836031440L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private int version;

  @Column(name = "position_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long positionPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "position_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  private PositionView positionView;
}
