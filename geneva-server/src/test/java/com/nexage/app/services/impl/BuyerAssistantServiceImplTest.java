package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.nexage.app.services.BuyerService;
import com.nexage.app.util.assemblers.BuyerTrafficConfigAssembler;
import com.nexage.countryservice.CountryService;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

@ExtendWith(MockitoExtension.class)
class BuyerAssistantServiceImplTest {

  @Mock private BuyerTrafficConfigAssembler buyerTrafficConfigAssembler;
  @Mock private BuyerService buyerService;
  @Mock private CountryService countryService;

  @Mock
  @Qualifier("coreDS")
  private DataSource coreJdbcTemplate;

  @Mock
  @Qualifier("dwDS")
  private DataSource dwJdbcTemplate;

  @Mock private Connection connection;

  @InjectMocks private BuyerAssistantServiceImpl buyerAssistantService;

  @Test
  void shouldGetNullBuyerTrafficConfigWhenBidderConfigsIsNull() {
    // given
    given(buyerService.getAllBidderConfigsByCompanyPid(anyLong())).willReturn(List.of());

    // when
    var result = buyerAssistantService.getBuyerTrafficConfig(1L);

    // then
    assertNull(result);
  }
}
