package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * This class represents an minimum data view of Tags
 *
 * @see Tag
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Immutable
public class TagView implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long pid;
  private String name;
  private String adsourceName;
  private Status status = Status.ACTIVE;
  private Long positionId;
  private Double floor;
  private Long siteId;
  private Long buyerId;
  private String ecpmProvision;
}
