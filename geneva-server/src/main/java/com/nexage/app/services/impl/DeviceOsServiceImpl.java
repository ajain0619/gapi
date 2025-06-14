package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.DeviceOsRepository;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.DeviceOsDTO;
import com.nexage.app.mapper.DeviceOsDTOMapper;
import com.nexage.app.services.DeviceOsService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DeviceOsServiceImpl implements DeviceOsService {
  private final DeviceOsRepository deviceOsRepository;

  @Autowired
  public DeviceOsServiceImpl(DeviceOsRepository deviceOsRepository) {
    this.deviceOsRepository = deviceOsRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DeviceOsDTO> findAllByName(String qt, Set<String> qf, Pageable pageable) {
    return deviceOsRepository
        .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
        .map(DeviceOsDTOMapper.MAPPER::map);
  }
}
