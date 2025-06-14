package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.GlobalConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, Long> {

  Optional<GlobalConfig> findByProperty(String property);

  default Optional<GlobalConfig> findByProperty(GlobalConfigProperty property) {
    return findByProperty(property.getPropertyName());
  }
}
