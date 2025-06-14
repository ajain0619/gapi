package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.repository.FeeAdjustmentRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/fee-adjustment-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class FeeAdjustmentSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final long PID = 1L;

  @Autowired private FeeAdjustmentRepository feeAdjustmentRepository;

  @Test
  void shouldFeeAdjustmentSpecificationQfUnspecifiedOrQtUnspecified() {
    assertNull(FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(null, null));
    assertNull(FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(Set.of("name"), null));
    assertNull(FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(null, "anything"));
    assertNull(FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(Set.of(), "anything"));
    assertNull(FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(Set.of("name"), ""));
  }

  @Test
  void shouldFeeAdjustmentSpecificationUnsupportedQueryFields() {
    List<Set<String>> fieldNames =
        List.of(
            Set.of("pid"),
            Set.of("demandFeeAdjustment"),
            Set.of("inclusive"),
            Set.of("version"),
            Set.of("enabled"),
            Set.of("description"),
            Set.of("entityName"),
            Set.of("UNKNOWN"),
            Set.of("pid", "name"),
            Set.of("pid", "demandFeeAdjustment"));

    for (Set<String> qf : fieldNames) {
      GenevaValidationException specificationException =
          assertThrows(
              GenevaValidationException.class,
              () -> FeeAdjustmentSpecification.withQueryFieldsAndSearchTerm(qf, "anything"));

      assertEquals(
          CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE,
          specificationException.getErrorCode());
    }
  }

  @Test
  void shouldFeeAdjustmentSpecificationEnabledUnspecified() {
    assertNull(FeeAdjustmentSpecification.withEnabled(null));
  }

  @Test
  void shouldGetOnlyEnabledFeeAdjustmentValues() {
    // when
    List<FeeAdjustment> values =
        feeAdjustmentRepository.findAll(FeeAdjustmentSpecification.withEnabled(Boolean.TRUE));

    // then
    assertEquals(2, values.size());
  }

  @Test
  void shouldReturnSpecificationWithQueryFieldsAndSearchTermAndEnabled() {
    // given
    Specification<FeeAdjustment> spec =
        FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(
            Set.of("name"), "fee-adjustment-repository-1", true);

    // when
    List<FeeAdjustment> result = feeAdjustmentRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }
}
