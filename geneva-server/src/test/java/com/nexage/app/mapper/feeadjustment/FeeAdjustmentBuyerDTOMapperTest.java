package com.nexage.app.mapper.feeadjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentBuyer;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentBuyerDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import io.vavr.Tuple;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeeAdjustmentBuyerDTOMapperTest {

  @Mock private FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepositoryMock;

  @Test
  void testFeeAdjustmentBuyerToFeeAdjustmentBuyerDTO() {
    FeeAdjustment feeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1234",
                true,
                0.1234,
                4321,
                false,
                "A test fee adjustment.",
                Date.from(Instant.EPOCH))
            .addBuyer(13L, new FeeAdjustmentCompanyView(33L, "buyer-1", CompanyType.BUYER))
            .getInstance();

    FeeAdjustmentBuyer input = feeAdjustment.getFeeAdjustmentBuyers().get(0);
    FeeAdjustmentBuyerDTO expectedOutput =
        FeeAdjustmentBuyerDTO.builder().buyerPid(33L).buyerName("buyer-1").build();

    assertEquals(expectedOutput, FeeAdjustmentBuyerDTOMapper.MAPPER.map(input));
  }

  @Test
  void testFeeAdjustmentBuyerDTOToFeeAdjustmentBuyer() {
    Map<Long, FeeAdjustmentCompanyView> feeAdjustmentCompanyViewMap =
        Map.of(33L, new FeeAdjustmentCompanyView(33L, "buyer-1", CompanyType.BUYER));

    FeeAdjustment feeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1234",
                true,
                0.1234,
                4321,
                false,
                "A test fee adjustment.",
                Date.from(Instant.EPOCH))
            .addBuyer(13L, feeAdjustmentCompanyViewMap.get(33L))
            .getInstance();

    FeeAdjustmentBuyerDTO input =
        FeeAdjustmentBuyerDTO.builder().buyerPid(33L).buyerName("buyer-1").build();
    FeeAdjustmentBuyer expectedOutput = feeAdjustment.getFeeAdjustmentBuyers().get(0);

    when(feeAdjustmentCompanyViewRepositoryMock.findById(any(Long.class)))
        .thenAnswer(
            invocationOnMock ->
                Optional.of(feeAdjustmentCompanyViewMap.get(invocationOnMock.getArgument(0))));

    assertEquals(
        Set.of(Tuple.of(expectedOutput.getFeeAdjustment(), expectedOutput.getBuyer())),
        FeeAdjustmentBuyerDTOMapper.MAPPER
            .map(List.of(input), feeAdjustment, feeAdjustmentCompanyViewRepositoryMock)
            .stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getFeeAdjustment(), feeAdjustmentBuyer.getBuyer()))
            .collect(Collectors.toSet()));
  }
}
