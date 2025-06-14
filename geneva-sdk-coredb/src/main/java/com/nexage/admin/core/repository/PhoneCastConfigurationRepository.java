package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.PhoneCastConfiguration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneCastConfigurationRepository
    extends JpaRepository<PhoneCastConfiguration, String>,
        JpaSpecificationExecutor<PhoneCastConfiguration> {

  Optional<PhoneCastConfiguration> findByConfigKey(String configKey);
}
