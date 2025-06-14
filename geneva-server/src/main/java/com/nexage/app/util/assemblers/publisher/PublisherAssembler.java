package com.nexage.app.util.assemblers.publisher;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.util.assemblers.context.NullableContext;
import java.util.Set;

public interface PublisherAssembler {

  PublisherDTO make(final NullableContext context, final Company model);

  PublisherDTO make(final NullableContext context, final Company model, final Set<String> fields);

  Company apply(final NullableContext context, final Company company, final PublisherDTO dto);

  Company applyTransparencySettings(final Company company, final PublisherDTO dto);

  Company applyHbPartnerAttributes(final Company company, final PublisherDTO dto);
}
