package com.nexage.app.mapper;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SetMapper {

  SetMapper MAPPER = Mappers.getMapper(SetMapper.class);

  /**
   * Maps a {@link Set<String>} to a CSV {@link String}.
   *
   * @param strings {@link Set<String>}
   * @return CSV {@link java.lang.String}. Returns null if {@link Set} is null or empty.
   */
  default String map(Set<String> strings) {
    if (CollectionUtils.isEmpty(strings)) {
      return null;
    }
    return strings.stream().collect(Collectors.joining(","));
  }

  /**
   * Maps a CSV {@link String} to a {@link Set<String>}.
   *
   * @param string CSV String
   * @return {@link Set<String>}. Returns null if {@link String} is null or empty
   */
  default Set<String> map(String string) {
    if (StringUtils.isEmpty(string)) {
      return Collections.emptySet();
    }
    return Set.of(string.split(","));
  }
}
