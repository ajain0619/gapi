package com.nexage.app.mapper;

import com.nexage.admin.core.model.IdentityProvider;
import com.nexage.app.dto.IdentityProviderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IdentityProviderDTOMapper {
  IdentityProviderDTOMapper MAPPER = Mappers.getMapper(IdentityProviderDTOMapper.class);

  IdentityProviderDTO map(IdentityProvider source);
}
