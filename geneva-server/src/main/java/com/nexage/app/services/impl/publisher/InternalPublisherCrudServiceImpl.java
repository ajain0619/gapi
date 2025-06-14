package com.nexage.app.services.impl.publisher;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerCompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.publisher.InternalPublisherCrudService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.publisher.InternalPublisherAssembler;
import com.nexage.app.util.assemblers.publisher.PublisherAssembler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Publisher crud service for internal users. Contains ability to manage publisher. */
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcManagerNexage()")
public class InternalPublisherCrudServiceImpl extends PublisherCrudServiceImpl
    implements InternalPublisherCrudService {

  private final InternalPublisherAssembler internalPublisherAssembler;
  private final BeanValidationService beanValidationService;

  public InternalPublisherCrudServiceImpl(
      CompanyService companyService,
      RtbProfileGroupService rtbProfileGroupService,
      TransparencyService transparencyService,
      UserContext userContext,
      RTBProfileService rtbProfileService,
      HbPartnerCompanyService hbPartnerCompanyService,
      CompanyRepository companyRepository,
      RevenueShareUpdateValidator revenueShareUpdateValidator,
      InternalPublisherAssembler internalPublisherAssembler,
      BeanValidationService beanValidationService) {
    super(
        companyService,
        rtbProfileGroupService,
        transparencyService,
        userContext,
        rtbProfileService,
        hbPartnerCompanyService,
        companyRepository,
        revenueShareUpdateValidator);
    this.internalPublisherAssembler = internalPublisherAssembler;
    this.beanValidationService = beanValidationService;
  }

  @Override
  protected PublisherAssembler getAssembler() {
    return internalPublisherAssembler;
  }

  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()")
  public PublisherDTO read(Long entityIdentifier) {
    return super.read(entityIdentifier);
  }

  @Override
  public PublisherDTO create(PublisherDTO inPublisher) {
    beanValidationService.validate(inPublisher);
    return super.create(inPublisher);
  }

  @Override
  public PublisherDTO update(PublisherDTO inPublisher, Long entityIdentifier) {
    beanValidationService.validate(inPublisher);
    return super.update(inPublisher, entityIdentifier);
  }
}
