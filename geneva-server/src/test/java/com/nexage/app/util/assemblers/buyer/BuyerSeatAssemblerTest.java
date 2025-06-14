package com.nexage.app.util.assemblers.buyer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class BuyerSeatAssemblerTest {

  @Mock private BuyerGroupRepository buyerGroupRepository;
  @Mock private CompanyRepository companyRepository;
  @InjectMocks private BuyerSeatAssembler buyerSeatAssembler;

  private static final long COMPANY_PID = 4L;
  private static final long BUYER_GROUP_PID = 2L;
  private static final long BUYER_SEAT_PID = 1L;
  private static final int VERSION = 3;

  @Test
  void shouldReturnBuyerSeatTransientEntity() {
    // given

    Company company = new Company();
    company.setPid(COMPANY_PID);
    BuyerGroup buyerGroup = new BuyerGroup();
    buyerGroup.setPid(BUYER_GROUP_PID);
    BuyerSeatDTO dto = getBuyerSeatDTO();

    when(companyRepository.getOne(COMPANY_PID)).thenReturn(company);
    when(buyerGroupRepository.getOne(BUYER_GROUP_PID)).thenReturn(buyerGroup);

    // when
    BuyerSeat entity = buyerSeatAssembler.transientEntity(COMPANY_PID, dto);

    // then
    assertEquals(company, entity.getCompany());
    assertEquals(buyerGroup, entity.getBuyerGroup());
    assertEquals(dto.getName(), entity.getName());
    assertEquals(dto.getSeat(), entity.getSeat());
    assertEquals(dto.isEnabled(), entity.isEnabled());
    assertEquals(dto.getBuyerTransparencyDataFeedPid(), entity.getBuyerTransparencyDataFeedPid());
    assertEquals(dto.isBuyerTransparencyFeedEnabled(), entity.getBuyerTransparencyFeedEnabled());
    assertNull(entity.getPid());
    assertNull(entity.getVersion());
    verify(companyRepository).getOne(COMPANY_PID);
    verify(buyerGroupRepository).getOne(BUYER_GROUP_PID);
  }

  @Test
  void shouldReturnUpdatedBuyerSeat() {
    // given
    BuyerGroup buyerGroup = new BuyerGroup();
    buyerGroup.setPid(BUYER_GROUP_PID);
    BuyerSeatDTO dto = getBuyerSeatDTO();
    BuyerSeat entity = new BuyerSeat();
    entity.setPid(1L);
    entity.setVersion(3);

    when(buyerGroupRepository.getOne(BUYER_GROUP_PID)).thenReturn(buyerGroup);

    // when
    entity = buyerSeatAssembler.apply(entity, dto);

    // then
    assertEquals(buyerGroup, entity.getBuyerGroup());
    assertEquals(dto.getName(), entity.getName());
    assertEquals(dto.getSeat(), entity.getSeat());
    assertEquals(dto.isEnabled(), entity.isEnabled());
    assertEquals(dto.getBuyerTransparencyDataFeedPid(), entity.getBuyerTransparencyDataFeedPid());
    assertEquals(dto.isBuyerTransparencyFeedEnabled(), entity.getBuyerTransparencyFeedEnabled());
    assertEquals(dto.getPid(), entity.getPid());
    assertEquals(dto.getVersion(), entity.getVersion());
    verify(buyerGroupRepository).getOne(BUYER_GROUP_PID);
  }

  private BuyerSeatDTO getBuyerSeatDTO() {
    return new BuyerSeatDTO(
        BUYER_SEAT_PID,
        "buyer-seat-name",
        "seat",
        true,
        BUYER_GROUP_PID,
        VERSION,
        COMPANY_PID,
        true,
        5L);
  }
}
