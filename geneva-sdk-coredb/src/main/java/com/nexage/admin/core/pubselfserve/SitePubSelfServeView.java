package com.nexage.admin.core.pubselfserve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.Mode;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

@Data
@Table(name = "site")
@Immutable
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SitePubSelfServeView implements Serializable {

  @EqualsAndHashCode.Include @ToString.Include @Column @Id private long pid;

  @EqualsAndHashCode.Include @ToString.Include @Column private String name;

  @Column
  @Enumerated(EnumType.STRING)
  private Type type;

  @Column(name = "company_pid")
  private long pubPid;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "sitePid")
  @Where(clause = "status >= 0")
  @MapKey(name = "name")
  private Map<String, PositionPubSelfServeView> positions = new HashMap<>();

  @Column
  @Enumerated(EnumType.STRING)
  private Platform platform;

  @JsonIgnore
  @Column(name = "status")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @JsonIgnore
  @Column(name = "live")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.ModeEnumType")
  private Mode mode;

  @Column(name = "dcn", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String dcn;
}
