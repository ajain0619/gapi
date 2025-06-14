package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BDRAdvertiserServiceImplTest {

  @Mock private BDRAdvertiserRepository bdrAdvertiserRepository;
  @InjectMocks private BDRAdvertiserServiceImpl bdrAdvertiserService;
  private final long companyPid = 1L;

  @Test
  void shouldGetAdvertisersForCompany() {
    // given
    when(bdrAdvertiserRepository.findByCompanyPid(companyPid))
        .thenReturn(List.of(new BDRAdvertiser()));

    // when
    List<BDRAdvertiser> advertisers = bdrAdvertiserService.getAdvertisersForCompany(companyPid);

    // then
    assertEquals(1, advertisers.size());
    verify(bdrAdvertiserRepository, times(1)).findByCompanyPid(companyPid);
    verifyNoMoreInteractions(bdrAdvertiserRepository);
  }
}
