package com.nexage.app.services.impl.publisher;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerCompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.publisher.ExternalPublisherCrudService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.publisher.ExternalPublisherAssembler;
import com.nexage.app.util.assemblers.publisher.PublisherAssembler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Publisher crud service for external users. Contains restricted ability to manage publisher. */
@Service
@Transactional
public class ExternalPublisherCrudServiceImpl extends PublisherCrudServiceImpl
    implements ExternalPublisherCrudService {

  private final ExternalPublisherAssembler externalPublisherAssembler;

  public ExternalPublisherCrudServiceImpl(
      CompanyService companyService,
      RtbProfileGroupService rtbProfileGroupService,
      TransparencyService transparencyService,
      UserContext userContext,
      RTBProfileService rtbProfileService,
      HbPartnerCompanyService hbPartnerCompanyService,
      CompanyRepository companyRepository,
      RevenueShareUpdateValidator revenueShareUpdateValidator,
      ExternalPublisherAssembler externalPublisherAssembler) {
    super(
        companyService,
        rtbProfileGroupService,
        transparencyService,
        userContext,
        rtbProfileService,
        hbPartnerCompanyService,
        companyRepository,
        revenueShareUpdateValidator);
    this.externalPublisherAssembler = externalPublisherAssembler;
  }

  @Override
  protected PublisherAssembler getAssembler() {
    return externalPublisherAssembler;
  }

  @Override
  public PublisherDTO create(PublisherDTO publisher) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Long entityIdentifier) {
    throw new UnsupportedOperationException();
  }
}
