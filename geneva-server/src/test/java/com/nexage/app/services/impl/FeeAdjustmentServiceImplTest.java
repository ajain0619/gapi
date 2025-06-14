package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment_;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.admin.core.repository.FeeAdjustmentRepository;
import com.nexage.admin.core.specification.CustomSearchSpecification;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentBuyerDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentSellerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class FeeAdjustmentServiceImplTest {

  @Mock private FeeAdjustmentRepository feeAdjustmentRepositoryMock;

  @Mock private FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepositoryMock;

  @InjectMocks private FeeAdjustmentServiceImpl feeAdjustmentServiceImpl;

  @Test
  void testCreateFeeAdjustment() {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(0)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(0)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();

    Map<Long, FeeAdjustmentCompanyView> companyPidToCompanyViewMap =
        Map.of(
            1L, new FeeAdjustmentCompanyView(1L, "company-1", CompanyType.SELLER),
            2L, new FeeAdjustmentCompanyView(2L, "company-2", CompanyType.BUYER),
            3L, new FeeAdjustmentCompanyView(3L, "company-3", CompanyType.BUYER));

    when(feeAdjustmentCompanyViewRepositoryMock.findById(anyLong()))
        .thenAnswer(
            invocationOnMock ->
                Optional.ofNullable(
                    companyPidToCompanyViewMap.get(invocationOnMock.getArgument(0))));

    when(feeAdjustmentRepositoryMock.saveAndFlush(any(FeeAdjustment.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    assertEquals(
        outboundFeeAdjustmentDTO, feeAdjustmentServiceImpl.create(inboundFeeAdjustmentDTO));
  }

  @Test
  void testUpdateFeeAdjustment() {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1-updated")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(0)
            .enabled(true)
            .description("An updated fee adjustment.")
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();
    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1-updated")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(0)
            .enabled(true)
            .description("An updated fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();

    Map<Long, FeeAdjustmentCompanyView> companyPidToCompanyViewMap =
        Map.of(
            1111L,
            new FeeAdjustmentCompanyView(1111L, "existing-company-1", CompanyType.SELLER),
            1112L,
            new FeeAdjustmentCompanyView(1112L, "existing-company-2", CompanyType.BUYER),
            1L,
            new FeeAdjustmentCompanyView(1L, "company-1", CompanyType.SELLER),
            2L,
            new FeeAdjustmentCompanyView(2L, "company-2", CompanyType.BUYER),
            3L,
            new FeeAdjustmentCompanyView(3L, "company-3", CompanyType.BUYER));

    FeeAdjustment existingFeeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1",
                false,
                0.0,
                0,
                false,
                "A fee adjustment.",
                Timestamp.valueOf("2020-08-31 12:00:00"))
            .addSeller(1111L, companyPidToCompanyViewMap.get(1111L))
            .addBuyer(1112L, companyPidToCompanyViewMap.get(1112L))
            .getInstance();

    when(feeAdjustmentRepositoryMock.findById(inboundFeeAdjustmentDTO.getPid()))
        .thenReturn(Optional.of(existingFeeAdjustment));

    when(feeAdjustmentCompanyViewRepositoryMock.findById(anyLong()))
        .thenAnswer(
            invocationOnMock ->
                Optional.ofNullable(
                    companyPidToCompanyViewMap.get(invocationOnMock.<Long>getArgument(0))));

    when(feeAdjustmentRepositoryMock.saveAndFlush(any(FeeAdjustment.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    assertEquals(
        outboundFeeAdjustmentDTO, feeAdjustmentServiceImpl.update(inboundFeeAdjustmentDTO));
  }

  @Test
  void testUpdateFeeAdjustmentDoesNotExist() {
    FeeAdjustmentDTO inboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(null)
            .enabled(true)
            .feeAdjustmentSellers(
                List.of(FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName(null).build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName(null).build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName(null).build()))
            .build();

    var notFoundException =
        assertThrows(
            GenevaValidationException.class,
            () -> feeAdjustmentServiceImpl.update(inboundFeeAdjustmentDTO));

    assertEquals(
        ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND, notFoundException.getErrorCode());
  }

  @Test
  void testGetFeeAdjustment() {
    FeeAdjustment feeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                1234L,
                "fee-adjustment-1",
                true,
                0.1,
                0,
                true,
                "A simple fee adjustment.",
                Date.from(Instant.now()))
            .addSeller(1L, new FeeAdjustmentCompanyView(1L, "company-1", CompanyType.SELLER))
            .addBuyer(1L, new FeeAdjustmentCompanyView(2L, "company-2", CompanyType.BUYER))
            .addBuyer(2L, new FeeAdjustmentCompanyView(3L, "company-3", CompanyType.BUYER))
            .getInstance();

    FeeAdjustmentDTO outboundFeeAdjustmentDTO =
        FeeAdjustmentDTO.builder()
            .pid(1234L)
            .name("fee-adjustment-1")
            .inclusive(true)
            .demandFeeAdjustment(0.1)
            .version(0)
            .enabled(true)
            .description("A simple fee adjustment.")
            .feeAdjustmentSellers(
                List.of(
                    FeeAdjustmentSellerDTO.builder().sellerPid(1L).sellerName("company-1").build()))
            .feeAdjustmentBuyers(
                List.of(
                    FeeAdjustmentBuyerDTO.builder().buyerPid(2L).buyerName("company-2").build(),
                    FeeAdjustmentBuyerDTO.builder().buyerPid(3L).buyerName("company-3").build()))
            .build();

    when(feeAdjustmentRepositoryMock.findById(feeAdjustment.getPid()))
        .thenReturn(Optional.of(feeAdjustment));

    assertEquals(outboundFeeAdjustmentDTO, feeAdjustmentServiceImpl.get(feeAdjustment.getPid()));
  }

  @Test
  void testGetFeeAdjustmentDoesNotExist() {
    when(feeAdjustmentRepositoryMock.findById(1234L)).thenReturn(Optional.empty());

    var notFoundException =
        assertThrows(GenevaValidationException.class, () -> feeAdjustmentServiceImpl.get(1234L));

    assertEquals(
        ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND, notFoundException.getErrorCode());
  }

  @Test
  void testGetAllQfQtEnabledPagedFeeAdjustments() {
    Page<FeeAdjustment> feeAdjustmentPage =
        new PageImpl<>(
            List.of(
                new TestObjectsFactory.FeeAdjustmentBuilder(
                        1L,
                        "fee-adjustment-1",
                        true,
                        0.1,
                        0,
                        true,
                        "A test fee adjustment.",
                        Date.from(Instant.now()))
                    .addBuyer(1L, new FeeAdjustmentCompanyView(1L, "buyer-1", CompanyType.BUYER))
                    .getInstance(),
                new TestObjectsFactory.FeeAdjustmentBuilder(
                        2L,
                        "fee-adjustment-2",
                        true,
                        0.2,
                        0,
                        true,
                        "A test fee adjustment.",
                        Date.from(Instant.now()))
                    .addBuyer(2L, new FeeAdjustmentCompanyView(2L, "buyer-2", CompanyType.BUYER))
                    .getInstance()));
    Specification<FeeAdjustment> specification =
        new CustomSearchSpecification.Builder<FeeAdjustment>()
            .with("name", "fee-adjustment")
            .build()
            .and(
                (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(FeeAdjustment_.ENABLED), true));

    Page<FeeAdjustmentDTO> outboundFeeAdjustmentSummaryDTOPage =
        new PageImpl<>(
            List.of(
                FeeAdjustmentDTO.builder()
                    .pid(1L)
                    .name("fee-adjustment-1")
                    .inclusive(true)
                    .demandFeeAdjustment(0.1)
                    .version(0)
                    .enabled(true)
                    .description("A test fee adjustment.")
                    .entityName("buyer-1")
                    .build(),
                FeeAdjustmentDTO.builder()
                    .pid(2L)
                    .name("fee-adjustment-2")
                    .inclusive(true)
                    .demandFeeAdjustment(0.2)
                    .version(0)
                    .enabled(true)
                    .description("A test fee adjustment.")
                    .entityName("buyer-2")
                    .build()));

    when(feeAdjustmentRepositoryMock.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(feeAdjustmentPage);

    assertEquals(
        outboundFeeAdjustmentSummaryDTOPage.get().collect(Collectors.toSet()),
        feeAdjustmentServiceImpl
            .getAll(Set.of("name"), "fee-adjustment", true, Pageable.unpaged())
            .get()
            .collect(Collectors.toSet()));
  }

  @Test
  void testDeleteFeeAdjustment() {
    when(feeAdjustmentRepositoryMock.existsById(1234L)).thenReturn(true);

    feeAdjustmentServiceImpl.delete(1234L);

    verify(feeAdjustmentRepositoryMock).deleteById(1234L);
  }

  @Test
  void testDeleteFeeAdjustmentDoesNotExist() {
    when(feeAdjustmentRepositoryMock.existsById(1234L)).thenReturn(false);

    var notFoundException =
        assertThrows(GenevaValidationException.class, () -> feeAdjustmentServiceImpl.delete(1234L));

    assertEquals(
        ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND, notFoundException.getErrorCode());
  }
}
