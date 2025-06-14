package com.nexage.app.mapper;

import com.nexage.admin.core.enums.AdType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdTypeMapper {

  AdTypeMapper MAPPER = Mappers.getMapper(AdTypeMapper.class);

  default String map(Set<AdType> adTypes) {
    if (CollectionUtils.isEmpty(adTypes)) {
      return null;
    }
    return adTypes.stream().map(AdType::getName).collect(Collectors.joining(","));
  }

  default Set<AdType> map(String adTypes) {
    if (StringUtils.isEmpty(adTypes)) {
      return Collections.emptySet();
    }
    return Arrays.stream(adTypes.split(","))
        .map(AdType::getValueFromName)
        .collect(Collectors.toSet());
  }
}
