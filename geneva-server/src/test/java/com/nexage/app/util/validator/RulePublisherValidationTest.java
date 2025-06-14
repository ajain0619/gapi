package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RulePublisherValidationTest {

  @Mock private CompanyRepository companyRepository;

  @InjectMocks private RulePublisherValidation validation;

  @Test
  void shouldBeValidWhenDataIsValidAndPublishersExistAndActive() {
    when(companyRepository.findSellersWithSpecificPids(any()))
        .thenReturn(Set.of(createCompany(121L, "Company1"), createCompany(123L, "Company2")));

    boolean result = validation.isValid("121,123");

    assertTrue(result);
  }

  @Test
  void shouldBeInvalidWhenDataIsValidButPublisherDoesNotExists() {
    when(companyRepository.findSellersWithSpecificPids(any()))
        .thenReturn(Set.of(createCompany(121L, "Company1")));

    boolean result = validation.isValid("121,123");

    assertFalse(result);
  }

  @Test
  void shouldBeInvalidWhenDataIsInvalid() {
    assertFalse(validation.isValid("abc,123"));
  }

  @Test
  void shouldBeInvalidWhenDataContainsDuplicates() {
    assertFalse(validation.isValid("121,121"));
  }

  private Company createCompany(Long pid, String name) {
    Company company = new Company();
    company.setPid(pid);
    company.setName(name);
    return company;
  }
}
