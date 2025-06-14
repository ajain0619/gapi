package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.DeviceTypeRepository;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.DeviceTypeDTO;
import com.nexage.app.mapper.DeviceTypeDTOMapper;
import com.nexage.app.services.DeviceTypeService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DeviceTypeServiceImpl implements DeviceTypeService {
  private final DeviceTypeRepository deviceTypeRepository;

  @Autowired
  public DeviceTypeServiceImpl(DeviceTypeRepository deviceTypeRepository) {
    this.deviceTypeRepository = deviceTypeRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DeviceTypeDTO> findAll(String qt, Set<String> qf, Pageable pageable) {
    return deviceTypeRepository
        .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
        .map(DeviceTypeDTOMapper.MAPPER::map);
  }
}
