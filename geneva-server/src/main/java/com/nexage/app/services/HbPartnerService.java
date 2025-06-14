package com.nexage.app.services;

import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.HbPartnerRequestDTO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface HbPartnerService {

  Page<HbPartnerDTO> getHbPartners(HbPartnerRequestDTO request);

  HbPartnerDTO getHbPartner(Long pid);

  HbPartnerDTO createHbPartner(HbPartnerDTO hbPartnerDTO);

  HbPartnerDTO updateHbPartner(HbPartnerDTO hbPartnerDTO);

  void deactivateHbPartner(Long pid);

  List<Long> findPidsByCompanyPid(long companyPid);
}
