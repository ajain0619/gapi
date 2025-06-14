package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.publisher.ExternalPublisherAssembler;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalPublisherAssemblerTest {

  @Mock private Company companyMock;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private ExternalPublisherAttributesAssembler externalPublisherAttributesAssembler;
  @Mock private PublisherEligibleBiddersAssembler publisherEligibleBiddersAssembler;
  @Mock private UserContext userContext;

  private ExternalPublisherAssembler externalPublisherAssembler;

  @BeforeEach
  void setup() {
    externalPublisherAssembler =
        new ExternalPublisherAssembler(
            publisherEligibleBiddersAssembler,
            hbPartnerRepository,
            externalPublisherAttributesAssembler,
            userContext);
  }

  @Test
  void test_constructor_initialization() {
    // Verifying the constructor initialization
    assertNotNull(externalPublisherAssembler);
    assertNotNull(externalPublisherAttributesAssembler);
  }

  @Test
  void test_getPublisherAttributesAssembler() {
    // Verifying the getPublisherAttributesAssembler method
    PublisherAttributesAssembler result =
        externalPublisherAssembler.getPublisherAttributesAssembler();
    assertEquals(
        externalPublisherAttributesAssembler,
        result,
        "Should return the externalPublisherAttributesAssembler");
  }

  @Test
  void test_applyHbPartnerAttributes_nullDTO() {
    PublisherDTO publisher = new PublisherDTO();
    publisher.setHbPartnerAttributes(null);
    Company company = externalPublisherAssembler.applyHbPartnerAttributes(companyMock, publisher);
    assertEquals(
        Collections.emptySet(), company.getHbPartnerCompany(), "Hb Partner should be empty");
  }
}
