package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.TestObjectsFactory;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.specification.FeeAdjustmentSpecification;
import io.vavr.Tuple;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/fee-adjustment-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class FeeAdjustmentRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired public FeeAdjustmentRepository feeAdjustmentRepository;

  @Autowired public FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository;

  @Test
  void testFeeAdjustmentFindByIdAndCheckDatabaseCompatibility() {
    Optional<FeeAdjustment> optionalStoredFeeAdjustment1 = feeAdjustmentRepository.findById(1L);

    assertTrue(optionalStoredFeeAdjustment1.isPresent());

    FeeAdjustment storedFeeAdjustment1 = optionalStoredFeeAdjustment1.get();

    validateStoredFreeAdjustments(
        storedFeeAdjustment1, (Long) 1L, "fee-adjustment-repository-1", 0.1);
    assertEquals(true, storedFeeAdjustment1.getInclusive());
    assertEquals(true, storedFeeAdjustment1.getEnabled());

    assertEquals(
        Set.of(
            Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1112L), storedFeeAdjustment1),
            Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1113L), storedFeeAdjustment1)),
        storedFeeAdjustment1.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        Set.of(Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1111L), storedFeeAdjustment1)),
        storedFeeAdjustment1.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()));

    Optional<FeeAdjustment> optionalStoredFeeAdjustment2 = feeAdjustmentRepository.findById(2L);

    assertTrue(optionalStoredFeeAdjustment2.isPresent());

    FeeAdjustment storedFeeAdjustment2 = optionalStoredFeeAdjustment2.get();

    validateStoredFreeAdjustments(
        storedFeeAdjustment2, (Long) 2L, "fee-adjustment-repository-2", 0.2);
    assertEquals(true, storedFeeAdjustment2.getInclusive());
    assertEquals(false, storedFeeAdjustment2.getEnabled());

    assertEquals(
        Set.of(Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1113L), storedFeeAdjustment2)),
        storedFeeAdjustment2.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        Set.of(Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1111L), storedFeeAdjustment2)),
        storedFeeAdjustment2.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()));

    Optional<FeeAdjustment> optionalStoredFeeAdjustment3 = feeAdjustmentRepository.findById(3L);

    assertTrue(optionalStoredFeeAdjustment3.isPresent());

    FeeAdjustment storedFeeAdjustment3 = optionalStoredFeeAdjustment3.get();

    validateStoredFreeAdjustments(
        storedFeeAdjustment3, (Long) 3L, "fee-adjustment-repository-3", 0.3);
    assertEquals(false, storedFeeAdjustment3.getInclusive());
    assertEquals(true, storedFeeAdjustment3.getEnabled());

    assertEquals(
        Set.of(),
        storedFeeAdjustment3.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        Set.of(),
        storedFeeAdjustment3.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()));
  }

  private void validateStoredFreeAdjustments(
      FeeAdjustment storedFeeAdjustment1,
      Long adjustmentPid,
      String adjustmentName,
      Double demandFreeAdjustment) {
    assertEquals(adjustmentPid, storedFeeAdjustment1.getPid());
    assertEquals(adjustmentName, storedFeeAdjustment1.getName());
    assertEquals(
        demandFreeAdjustment, storedFeeAdjustment1.getDemandFeeAdjustment(), Double.MIN_VALUE);
    assertEquals((Integer) 0, storedFeeAdjustment1.getVersion());

    assertEquals(Timestamp.valueOf("2020-07-01 12:00:00"), storedFeeAdjustment1.getCreationDate());
    assertEquals(Timestamp.valueOf("2020-08-31 12:00:00"), storedFeeAdjustment1.getLastUpdate());
    assertEquals("A test fee adjustment.", storedFeeAdjustment1.getDescription());
  }

  @Test
  void testFeeAdjustmentCreate() {
    FeeAdjustment feeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                null, "fee-adjustment-3", true, 0.3, null, true, "A test fee adjustment.", null)
            .addSeller(null, feeAdjustmentCompanyViewRepository.getOne(1112L))
            .addSeller(null, feeAdjustmentCompanyViewRepository.getOne(1113L))
            .addBuyer(null, feeAdjustmentCompanyViewRepository.getOne(1111L))
            .getInstance();

    feeAdjustment = feeAdjustmentRepository.saveAndFlush(feeAdjustment);

    assertTrue(feeAdjustmentRepository.existsById(feeAdjustment.getPid()));

    Optional<FeeAdjustment> storedFeeAdjustment =
        feeAdjustmentRepository.findById(feeAdjustment.getPid());

    assertTrue(storedFeeAdjustment.isPresent());
    assertEquals(feeAdjustment.getPid(), storedFeeAdjustment.get().getPid());
    assertEquals(feeAdjustment.getName(), storedFeeAdjustment.get().getName());
    assertEquals(feeAdjustment.getInclusive(), storedFeeAdjustment.get().getInclusive());
    assertEquals(
        feeAdjustment.getDemandFeeAdjustment(),
        storedFeeAdjustment.get().getDemandFeeAdjustment(),
        Double.MIN_VALUE);
    assertEquals((Integer) 0, feeAdjustment.getVersion());
    assertEquals(feeAdjustment.getVersion(), storedFeeAdjustment.get().getVersion());
    assertEquals(feeAdjustment.getCreationDate(), storedFeeAdjustment.get().getCreationDate());
    assertEquals(feeAdjustment.getLastUpdate(), storedFeeAdjustment.get().getLastUpdate());
    assertEquals(
        feeAdjustment.getCreationDate(),
        feeAdjustment.getLastUpdate(),
        "The \"creation_date\" field should match the \"last_update\" field for a new entity.");
    assertEquals(feeAdjustment.getEnabled(), storedFeeAdjustment.get().getEnabled());
    assertEquals(feeAdjustment.getDescription(), storedFeeAdjustment.get().getDescription());

    assertEquals(
        feeAdjustment.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()),
        storedFeeAdjustment.get().getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        feeAdjustment.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()),
        storedFeeAdjustment.get().getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()));

    feeAdjustmentRepository.deleteById(feeAdjustment.getPid());
  }

  @Test
  void testFeeAdjustmentUpdate() {
    FeeAdjustment feeAdjustment =
        new TestObjectsFactory.FeeAdjustmentBuilder(
                2L,
                "fee-adjustment-2-updated",
                false,
                0.11,
                0,
                false,
                "A test fee adjustment updated.",
                null)
            .addSeller(null, feeAdjustmentCompanyViewRepository.getOne(1112L))
            .addSeller(3L, feeAdjustmentCompanyViewRepository.getOne(1113L))
            .getInstance();

    assertTrue(feeAdjustmentRepository.existsById(feeAdjustment.getPid()));

    Instant instantBeforeUpdate = Instant.now();

    feeAdjustment = feeAdjustmentRepository.saveAndFlush(feeAdjustment);

    Instant instantAfterUpdate = Instant.now();

    assertEquals((Long) 2L, feeAdjustment.getPid());

    Optional<FeeAdjustment> storedFeeAdjustment =
        feeAdjustmentRepository.findById(feeAdjustment.getPid());

    assertTrue(storedFeeAdjustment.isPresent());
    assertEquals(feeAdjustment.getPid(), storedFeeAdjustment.get().getPid());
    assertEquals(feeAdjustment.getName(), storedFeeAdjustment.get().getName());
    assertEquals(feeAdjustment.getInclusive(), storedFeeAdjustment.get().getInclusive());
    assertEquals(
        feeAdjustment.getDemandFeeAdjustment(),
        storedFeeAdjustment.get().getDemandFeeAdjustment(),
        Double.MIN_VALUE);
    assertEquals(feeAdjustment.getVersion(), storedFeeAdjustment.get().getVersion());
    assertEquals(feeAdjustment.getLastUpdate(), storedFeeAdjustment.get().getLastUpdate());
    assertTrue(
        (feeAdjustment.getLastUpdate().toInstant().isAfter(instantBeforeUpdate)
                || feeAdjustment.getLastUpdate().toInstant().equals(instantBeforeUpdate))
            && (feeAdjustment.getLastUpdate().toInstant().isBefore(instantAfterUpdate)
                || feeAdjustment.getLastUpdate().toInstant().equals(instantAfterUpdate)));
    assertEquals(feeAdjustment.getEnabled(), storedFeeAdjustment.get().getEnabled());
    assertEquals(feeAdjustment.getDescription(), storedFeeAdjustment.get().getDescription());

    assertEquals(
        feeAdjustment.getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()),
        storedFeeAdjustment.get().getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        feeAdjustment.getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getBuyer(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()),
        storedFeeAdjustment.get().getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getBuyer(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
  }

  @Test
  void testFeeAdjustmentGet() {
    Optional<FeeAdjustment> storedFeeAdjustment = feeAdjustmentRepository.findById(1L);

    assertTrue(storedFeeAdjustment.isPresent());
    assertEquals((Long) 1L, storedFeeAdjustment.get().getPid());
    assertEquals("fee-adjustment-repository-1", storedFeeAdjustment.get().getName());
    assertEquals(true, storedFeeAdjustment.get().getInclusive());
    assertEquals(0.1, storedFeeAdjustment.get().getDemandFeeAdjustment(), Double.MIN_VALUE);
    assertEquals((Integer) 0, storedFeeAdjustment.get().getVersion());
    assertEquals(true, storedFeeAdjustment.get().getEnabled());
    assertEquals("A test fee adjustment.", storedFeeAdjustment.get().getDescription());
    assertEquals(
        Timestamp.valueOf("2020-07-01 12:00:00"), storedFeeAdjustment.get().getCreationDate());
    assertEquals(
        Timestamp.valueOf("2020-08-31 12:00:00"), storedFeeAdjustment.get().getLastUpdate());

    assertEquals(
        Set.of(
            Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1112L), storedFeeAdjustment.get()),
            Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1113L), storedFeeAdjustment.get())),
        storedFeeAdjustment.get().getFeeAdjustmentSellers().stream()
            .map(
                feeAdjustmentSeller ->
                    Tuple.of(
                        feeAdjustmentSeller.getSeller(), feeAdjustmentSeller.getFeeAdjustment()))
            .collect(Collectors.toSet()));
    assertEquals(
        Set.of(
            Tuple.of(feeAdjustmentCompanyViewRepository.getOne(1111L), storedFeeAdjustment.get())),
        storedFeeAdjustment.get().getFeeAdjustmentBuyers().stream()
            .map(
                feeAdjustmentBuyer ->
                    Tuple.of(feeAdjustmentBuyer.getBuyer(), feeAdjustmentBuyer.getFeeAdjustment()))
            .collect(Collectors.toSet()));
  }

  @Test
  void testFeeAdjustmentGetAllQfQt() {
    Page<FeeAdjustment> storedFeeAdjustmentsPagedResult1 =
        feeAdjustmentRepository.findAll(
            FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(
                Set.of("name"), "repository-1", null),
            Pageable.unpaged());

    assertEquals(
        Set.of(feeAdjustmentRepository.getOne(1L)),
        storedFeeAdjustmentsPagedResult1.get().collect(Collectors.toSet()));

    Page<FeeAdjustment> storedFeeAdjustmentsPagedResult2 =
        feeAdjustmentRepository.findAll(
            FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(
                Set.of("name"), "repository", null),
            Pageable.unpaged());

    assertEquals(
        Set.of(
            feeAdjustmentRepository.getOne(1L),
            feeAdjustmentRepository.getOne(2L),
            feeAdjustmentRepository.getOne(3L)),
        storedFeeAdjustmentsPagedResult2.get().collect(Collectors.toSet()));
  }

  @Test
  void testFeeAdjustmentGetAllEnabled() {
    Page<FeeAdjustment> storedFeeAdjustmentsPagedResult1 =
        feeAdjustmentRepository.findAll(
            FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(null, null, true),
            Pageable.unpaged());

    assertEquals(
        Set.of(feeAdjustmentRepository.getOne(1L), feeAdjustmentRepository.getOne(3L)),
        storedFeeAdjustmentsPagedResult1.get().collect(Collectors.toSet()));

    Page<FeeAdjustment> storedFeeAdjustmentsPagedResult2 =
        feeAdjustmentRepository.findAll(
            FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(null, null, false),
            Pageable.unpaged());

    assertEquals(
        Set.of(feeAdjustmentRepository.getOne(2L)),
        storedFeeAdjustmentsPagedResult2.get().collect(Collectors.toSet()));
  }

  @Test
  void testFeeAdjustmentGetAllPaged() {
    Specification<FeeAdjustment> emptyQuerySpecification =
        FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(null, null, null);

    Page<FeeAdjustment> storedFeeAdjustmentsPage =
        feeAdjustmentRepository.findAll(emptyQuerySpecification, PageRequest.of(0, 10));

    assertEquals(
        Set.of(
            feeAdjustmentRepository.getOne(1L),
            feeAdjustmentRepository.getOne(2L),
            feeAdjustmentRepository.getOne(3L)),
        storedFeeAdjustmentsPage.get().collect(Collectors.toSet()));

    Page<FeeAdjustment> storedFeeAdjustmentsSmallPage1 =
        feeAdjustmentRepository.findAll(emptyQuerySpecification, PageRequest.of(0, 2));

    assertEquals(
        Set.of(feeAdjustmentRepository.getOne(1L), feeAdjustmentRepository.getOne(2L)),
        storedFeeAdjustmentsSmallPage1.get().collect(Collectors.toSet()));

    Page<FeeAdjustment> storedFeeAdjustmentsSmallPage2 =
        feeAdjustmentRepository.findAll(emptyQuerySpecification, PageRequest.of(1, 2));

    assertEquals(
        Set.of(feeAdjustmentRepository.getOne(3L)),
        storedFeeAdjustmentsSmallPage2.get().collect(Collectors.toSet()));
  }

  @Test
  void testFeeAdjustmentDelete() {
    assertTrue(feeAdjustmentRepository.existsById(1L));

    feeAdjustmentRepository.deleteById(1L);

    assertFalse(feeAdjustmentRepository.existsById(1L));
    assertFalse(feeAdjustmentRepository.findById(1L).isPresent());
  }
}
