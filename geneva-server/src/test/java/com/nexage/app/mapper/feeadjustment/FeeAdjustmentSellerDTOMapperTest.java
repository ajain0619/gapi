package com.nexage.app.mapper.feeadjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentSeller;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentSellerDTO;
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
class FeeAdjustmentSellerDTOMapperTest {

  @Mock private FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepositoryMock;

  @Test
  void testFeeAdjustmentSellerToFeeAdjustmentSellerDTO() {
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
            .addSeller(11L, new FeeAdjustmentCompanyView(31L, "seller-1", CompanyType.SELLER))
            .getInstance();

    FeeAdjustmentSeller input = feeAdjustment.getFeeAdjustmentSellers().get(0);
    FeeAdjustmentSellerDTO expectedOutput =
        FeeAdjustmentSellerDTO.builder().sellerPid(31L).sellerName("seller-1").build();

    assertEquals(expectedOutput, FeeAdjustmentSellerDTOMapper.MAPPER.map(input));
  }

  @Test
  void testFeeAdjustmentSellerDTOToFeeAdjustmentSeller() {
    Map<Long, FeeAdjustmentCompanyView> feeAdjustmentCompanyViewMap =
        Map.of(31L, new FeeAdjustmentCompanyView(31L, "seller-1", CompanyType.SELLER));

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
            .addSeller(11L, feeAdjustmentCompanyViewMap.get(31L))
            .getInstance();

    FeeAdjustmentSellerDTO input =
        FeeAdjustmentSellerDTO.builder().sellerPid(31L).sellerName("seller-1").build();
    FeeAdjustmentSeller expectedOutput = feeAdjustment.getFeeAdjustmentSellers().get(0);

    when(feeAdjustmentCompanyViewRepositoryMock.findById(any(Long.class)))
        .thenAnswer(
            invocationOnMock ->
                Optional.of(feeAdjustmentCompanyViewMap.get(invocationOnMock.getArgument(0))));

    assertEquals(
        Set.of(Tuple.of(expectedOutput.getFeeAdjustment(), expectedOutput.getSeller())),
        FeeAdjustmentSellerDTOMapper.MAPPER
            .map(List.of(input), feeAdjustment, feeAdjustmentCompanyViewRepositoryMock)
            .stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getFeeAdjustment(), feeAdjustmentSeller.getSeller()))
            .collect(Collectors.toSet()));
  }
}
