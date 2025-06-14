package com.nexage.app.web.publisher;

import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.services.publisher.InternalPublisherCrudService;
import com.nexage.app.web.BaseCrudController;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/pss/publisher")
@RestController
@RequestMapping(value = "/pss/publisher")
public class PublisherCrudController
    extends BaseCrudController<PublisherDTO, Long, InternalPublisherCrudService> {

  private final InternalPublisherCrudService internalPublisherCrudService;

  public PublisherCrudController(InternalPublisherCrudService internalPublisherCrudService) {
    this.internalPublisherCrudService = internalPublisherCrudService;
  }

  @Override
  protected InternalPublisherCrudService getService() {
    return internalPublisherCrudService;
  }
}
