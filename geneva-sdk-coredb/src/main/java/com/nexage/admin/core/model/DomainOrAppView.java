package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.ApprovalStatus;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * This class represents a minimum data view of Domain, App bundle, or App Alias
 *
 * @see AppAlias
 * @see AppBundleData
 * @see com.nexage.admin.core.model.filter.Domain
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Immutable
public class DomainOrAppView implements Serializable {
  private static final long serialVersionUID = 1L;
  private String domainOrApp;
  private ApprovalStatus approvalStatus;
}
