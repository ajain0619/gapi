package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.repository.PostAuctionDiscountRepository;
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
    scripts = "/data/repository/post-auction-discount-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class PostAuctionDiscountSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final long PID = 1L;

  @Autowired private PostAuctionDiscountRepository postAuctionDiscountRepository;

  @Test
  void shouldProduceNullWhenQueryFieldOrQueryTermAreBlank() {
    assertNull(PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(Set.of(), ""));
    assertNull(PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(null, null));
    assertNull(
        PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(Set.of("discountName"), ""));
    assertNull(
        PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(Set.of(), "any value"));
  }

  @Test
  void shouldThrowExceptionWithQueryFieldsAndSearchTermWhenUnsupportedQueryFieldValue() {
    // given
    Set<String> qf = Set.of("pid", "discountName");

    // when
    GenevaValidationException specificationException =
        assertThrows(
            GenevaValidationException.class,
            () -> PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(qf, "some_value"));

    // then
    assertEquals(
        CoreDBErrorCodes.CORE_DB_INVALID_QUERY_FIELD_PARAM_VALUE,
        specificationException.getErrorCode());
  }

  @Test
  void shouldFindAllWithQueryFieldsAndSearchTermWhenSupportedQueryFieldValue() {
    // given
    Set<String> qf = Set.of("discountName");
    Specification<PostAuctionDiscount> spec =
        PostAuctionDiscountSpecification.withQueryFieldsAndSearchTerm(
            qf, "post-auction-discount-3");

    // when
    List<PostAuctionDiscount> result = postAuctionDiscountRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
  }

  @Test
  void shouldProduceNullWhenEnabledIsNull() {
    // when
    Specification<PostAuctionDiscount> spec = PostAuctionDiscountSpecification.withEnabled(null);

    // then
    assertNull(spec);
  }

  @Test
  void shouldGetOnlyEnabledSpecificationValues() {
    // given
    Specification<PostAuctionDiscount> spec =
        PostAuctionDiscountSpecification.withEnabled(Boolean.TRUE);

    // when
    List<PostAuctionDiscount> result = postAuctionDiscountRepository.findAll(spec);

    // then
    assertEquals(4, result.size());
  }

  @Test
  void shouldFindAllWithQueryFieldsAndSearchTermAndEnabled() {
    // given
    Specification<PostAuctionDiscount> spec =
        PostAuctionDiscountSpecification.withQueryFieldsAndSearchTermAndEnabled(
            Set.of("discountName"), "post-auction-discount-1", true);

    // when
    List<PostAuctionDiscount> result = postAuctionDiscountRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }
}
