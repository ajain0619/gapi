package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DeviceOs;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceOsRepository
    extends JpaRepository<DeviceOs, Long>, JpaSpecificationExecutor<DeviceOs> {

  List<DeviceOs> findByNameIn(Collection<String> names);
}
