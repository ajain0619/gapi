package com.nexage.app.mapper.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexage.app.dto.seller.nativeads.BaseNativePlacementExtensionDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class NativePlacementExtensionDTOMapper {

  private final CustomObjectMapper objectMapper;

  @Named("convertToNativePlacementExtensionDto")
  public BaseNativePlacementExtensionDTO convertToNativePlacementExtensionDto(String nativeAsStr) {
    if (StringUtils.isNotBlank(nativeAsStr)) {
      try {
        return objectMapper.readValue(nativeAsStr, BaseNativePlacementExtensionDTO.class);
      } catch (IOException e) {
        final String message =
            "failed to convertToNativePlacementExtensionDto when given[" + nativeAsStr + "]";
        log.warn(message);
        throw new IllegalStateException(message);
      } catch (Exception ex) {
        log.error(ex);
        throw ex;
      }
    }
    return null;
  }

  @Named("convertToPosition")
  public String convertToPosition(BaseNativePlacementExtensionDTO dto) {
    String result;
    try {
      result = objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      final String message = "failed to convertToPosition when given [" + dto + "]";
      log.warn(message);
      throw new IllegalStateException(message);
    } catch (Exception ex) {
      log.error(ex);
      throw ex;
    }
    return result;
  }
}
