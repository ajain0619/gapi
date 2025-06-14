package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Table(name = "position")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Immutable
@Audited
public class TagPosition implements Serializable {

  private static final long serialVersionUID = 2848210031054039207L;

  @Id @GeneratedValue @EqualsAndHashCode.Include @ToString.Include private Long pid;
}
