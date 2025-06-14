package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "user_restrictedsite")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRestrictedSite implements Serializable {

  private static final long serialVersionUID = 1888820388744579567L;

  @EmbeddedId @EqualsAndHashCode.Include @ToString.Include private UserRestrictedSitePK pk;
}
