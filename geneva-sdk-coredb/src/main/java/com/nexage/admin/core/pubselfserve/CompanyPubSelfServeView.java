package com.nexage.admin.core.pubselfserve;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Data
@Table(name = "company")
@Immutable
@Entity
public class CompanyPubSelfServeView implements Serializable {

  @Column @Id private long pid;

  @Column private String name;

  @Column(name = "selfserve_allowed")
  private boolean selfServeAllowed;
}
