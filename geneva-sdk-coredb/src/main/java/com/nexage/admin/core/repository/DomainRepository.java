package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.filter.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository
    extends JpaRepository<Domain, Integer>, JpaSpecificationExecutor<Domain> {

  /**
   * Fetches a single Domain by domain name
   *
   * @param domain {@link Domain}
   * @return {@link Domain}
   */
  Domain findByDomain(String domain);
}
