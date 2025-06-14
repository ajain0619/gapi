package com.nexage.admin.core.specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.DealBidderConfigView_;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class DealBidderSpecificationTest {
  private Specification<DealBidderConfigView> spec;

  @BeforeEach
  void setUp() {
    String name = UUID.randomUUID().toString();
    Set<String> qf = new HashSet<>();
    qf.add(DealBidderConfigView_.NAME);
    spec = GeneralSpecification.withSearchCriteria(qf, name);
  }

  @Test
  void evaluateSpecification() {
    assertNotNull(spec);
    assertThat(spec, is(notNullValue()));
  }
}
