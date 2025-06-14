package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.IdentityProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityProviderRepository
    extends JpaRepository<IdentityProvider, Long>, JpaSpecificationExecutor<IdentityProvider> {}
