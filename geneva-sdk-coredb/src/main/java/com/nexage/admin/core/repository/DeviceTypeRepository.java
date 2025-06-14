package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DeviceType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeRepository
    extends JpaRepository<DeviceType, Long>, JpaSpecificationExecutor<DeviceType> {

  List<DeviceType> findByNameIn(Collection<String> names);

  boolean existsById(Integer id);
}
