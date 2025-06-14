package com.nexage.app.util.assemblers.publisher;

import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.ExternalPublisherAttributesAssembler;
import com.nexage.app.util.assemblers.PublisherAttributesAssembler;
import com.nexage.app.util.assemblers.PublisherEligibleBiddersAssembler;
import org.springframework.stereotype.Component;

@Component
public class ExternalPublisherAssembler extends BasePublisherAssembler {

  private final ExternalPublisherAttributesAssembler externalPublisherAttributesAssembler;

  public ExternalPublisherAssembler(
      PublisherEligibleBiddersAssembler publisherEligibleBiddersAssembler,
      HbPartnerRepository hbPartnerRepository,
      ExternalPublisherAttributesAssembler externalPublisherAttributesAssembler,
      UserContext userContext) {
    super(publisherEligibleBiddersAssembler, hbPartnerRepository, userContext);
    this.externalPublisherAttributesAssembler = externalPublisherAttributesAssembler;
  }

  @Override
  public PublisherAttributesAssembler getPublisherAttributesAssembler() {
    return externalPublisherAttributesAssembler;
  }
}
