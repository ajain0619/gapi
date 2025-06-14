package com.nexage.admin.core.pubselfserve;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Data
@Table(name = "ad_source")
@Immutable
@Entity
public class AdsourcePubSelfServeView implements Serializable {

  public enum AdSourceType {
    Mediation,
    RTB
  }

  @Column @Id private long pid;

  @Column private String name;

  @Column private String logo;
}
