package com.nexage.app.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.DoohScreen;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper(uses = {SetMapper.class, AdTypeMapper.class})
public interface DoohScreenDTOMapper {

  DoohScreenDTOMapper MAPPER = Mappers.getMapper(DoohScreenDTOMapper.class);

  /**
   * Map a JSON file to {@link List<DoohScreen>} collection
   *
   * @param screensFile {@link MultipartFile}
   * @return @{link List<{@link DoohScreen}></{@link>}
   * @throws IOException
   */
  default List<DoohScreenDTO> map(MultipartFile screensFile) throws IOException {
    if (Objects.isNull(screensFile)) {
      return Collections.emptyList();
    }
    byte[] bytes = screensFile.getBytes();
    return Arrays.asList(new ObjectMapper().readValue(bytes, DoohScreenDTO[].class));
  }

  /**
   * Map {@link List<DoohScreenDTO>} to {@link List<DoohScreen>}
   *
   * @param doohScreenDTOs {@link List<DoohScreenDTO>}
   * @return {@link List<DoohScreen>}
   */
  List<DoohScreen> map(List<DoohScreenDTO> doohScreenDTOs);

  DoohScreenDTO map(DoohScreen doohScreens);
}
