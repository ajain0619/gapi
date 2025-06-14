package com.nexage.app.mapper.feeadjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentBuyerDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentSellerDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeeAdjustmentDTOMapperTest {

  @Mock private FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepositoryMock;

  @Test
  void testFeeAdjustmentToFeeAdjustmentDTO() {
    FeeAdjustment input =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1234",
                true,
                0.1234,
                4321,
                false,
                "A test fee adjustment.",
                Date.from(Instant.EPOCH))
            .addSeller(11L, new FeeAdjustmentCompanyView(31L, "seller-1", CompanyType.SELLER))
            .addSeller(11L, new FeeAdjustmentCompanyView(32L, "seller-2", CompanyType.SELLER))
            .addBuyer(21L, new FeeAdjustmentCompanyView(33L, "buyer-1", CompanyType.BUYER))
            .getInstance();
    FeeAdjustmentDTO expectedOutput =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1234")
            .inclusive(true)
            .demandFeeAdjustment(0.1234)
            .version(4321)
            .enabled(false)
            .description("A test fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(31L).sellerName("seller-1").build(),
                    FeeAdjustmentSellerDTO.builder().sellerPid(32L).sellerName("seller-2").build()))
            .feeAdjustmentBuyers(
                List.of(FeeAdjustmentBuyerDTO.builder().buyerPid(33L).buyerName("buyer-1").build()))
            .build();

    assertEquals(expectedOutput, FeeAdjustmentDTOMapper.MAPPER.map(input, false));
  }

  @Test
  void testFeeAdjustmentToGetAllFeeAdjustmentDTO() {
    FeeAdjustment input =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1234",
                true,
                0.1234,
                4321,
                false,
                "A test fee adjustment.",
                Date.from(Instant.EPOCH))
            .addSeller(11L, new FeeAdjustmentCompanyView(31L, "seller-1", CompanyType.SELLER))
            .addSeller(11L, new FeeAdjustmentCompanyView(32L, "seller-2", CompanyType.SELLER))
            .addBuyer(21L, new FeeAdjustmentCompanyView(33L, "buyer-1", CompanyType.BUYER))
            .addBuyer(22L, new FeeAdjustmentCompanyView(34L, "buyer-2", CompanyType.BUYER))
            .getInstance();
    FeeAdjustmentDTO expectedOutput =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1234")
            .inclusive(true)
            .demandFeeAdjustment(0.1234)
            .version(4321)
            .enabled(false)
            .description("A test fee adjustment.")
            .entityName("buyer-1, buyer-2")
            .build();

    assertEquals(expectedOutput, FeeAdjustmentDTOMapper.MAPPER.map(input, true));
  }

  @Test
  void testFeeAdjustmentDTOToFeeAdjustment() {
    Map<Long, FeeAdjustmentCompanyView> feeAdjustmentCompanyViewMap =
        Map.of(
            31L, new FeeAdjustmentCompanyView(31L, "seller-1", CompanyType.SELLER),
            32L, new FeeAdjustmentCompanyView(32L, "seller-2", CompanyType.SELLER),
            33L, new FeeAdjustmentCompanyView(33L, "buyer-1", CompanyType.BUYER));

    FeeAdjustmentDTO input =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1234")
            .inclusive(true)
            .demandFeeAdjustment(0.1234)
            .version(4321)
            .enabled(false)
            .description("A test fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(31L).sellerName("seller-1").build(),
                    FeeAdjustmentSellerDTO.builder().sellerPid(32L).sellerName("seller-2").build()))
            .feeAdjustmentBuyers(
                List.of(FeeAdjustmentBuyerDTO.builder().buyerPid(33L).buyerName("buyer-1").build()))
            .build();
    FeeAdjustment expectedOutput =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1234",
                true,
                0.1234,
                4321,
                false,
                "A test fee adjustment.",
                Date.from(Instant.EPOCH))
            .addSeller(11L, feeAdjustmentCompanyViewMap.get(31L))
            .addSeller(11L, feeAdjustmentCompanyViewMap.get(32L))
            .addBuyer(21L, feeAdjustmentCompanyViewMap.get(33L))
            .getInstance();

    when(feeAdjustmentCompanyViewRepositoryMock.findById(any(Long.class)))
        .thenAnswer(
            invocationOnMock ->
                Optional.of(feeAdjustmentCompanyViewMap.get(invocationOnMock.getArgument(0))));

    FeeAdjustment feeAdjustment =
        FeeAdjustmentDTOMapper.MAPPER.map(
            input, new FeeAdjustment(), feeAdjustmentCompanyViewRepositoryMock);

    assertEquals(expectedOutput.getName(), feeAdjustment.getName());
    assertEquals(expectedOutput.getInclusive(), feeAdjustment.getInclusive());
    assertEquals(expectedOutput.getDemandFeeAdjustment(), feeAdjustment.getDemandFeeAdjustment());
    assertEquals(expectedOutput.getVersion(), feeAdjustment.getVersion());
    assertEquals(expectedOutput.getEnabled(), feeAdjustment.getEnabled());
    assertEquals(expectedOutput.getDescription(), feeAdjustment.getDescription());
    assertEquals(
        expectedOutput.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    List.of(
                        feeAdjustmentSeller.getFeeAdjustment(), feeAdjustmentSeller.getSeller()))
            .collect(Collectors.toSet()),
        feeAdjustment.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    List.of(
                        feeAdjustmentSeller.getFeeAdjustment(), feeAdjustmentSeller.getSeller()))
            .collect(Collectors.toSet()));
    assertEquals(
        expectedOutput.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentSeller ->
                    List.of(feeAdjustmentSeller.getFeeAdjustment(), feeAdjustmentSeller.getBuyer()))
            .collect(Collectors.toSet()),
        feeAdjustment.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentSeller ->
                    List.of(feeAdjustmentSeller.getFeeAdjustment(), feeAdjustmentSeller.getBuyer()))
            .collect(Collectors.toSet()));
  }
}
