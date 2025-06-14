package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.app.services.BDRAdvertiserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeatHolder()")
public class BDRAdvertiserServiceImpl implements BDRAdvertiserService {

  private final BDRAdvertiserRepository bdrAdvertiserRepository;

  @Override
  public List<BDRAdvertiser> getAdvertisersForCompany(long companyPid) {
    return bdrAdvertiserRepository.findByCompanyPid(companyPid);
  }
}
