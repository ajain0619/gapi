package com.nexage.app.dto.seller.nativeads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.util.ResourceLoader;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class NativePlacementExtensionDTODeserializerTest {

  private static final String INPUT_NATIVE_AD =
      "/data/nativeplacement/deserialize/native_ad_deserialization.json";
  private static final String INPUT_WEB_NATIVE_AD =
      "/data/nativeplacement/deserialize/web_native_ad_deserialization.json";

  ObjectMapper mapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void shoulDdeserializeNativeAd() {
    validateDeserialize(INPUT_NATIVE_AD, NativePlacementExtensionDTO.class);
  }

  @Test
  @SneakyThrows
  void shouldDeserializeWebNativeAd() {
    validateDeserialize(INPUT_WEB_NATIVE_AD, WebNativePlacementExtensionDTO.class);
  }

  @SneakyThrows
  private <T extends BaseNativePlacementExtensionDTO> void validateDeserialize(
      String inputStrPath, Class<T> expectedClass) {

    try (InputStream resourceAsStream = ResourceLoader.getResourceAsStream(inputStrPath)) {
      NativePlacementDTO nativePlacementDTO =
          mapper.readValue(resourceAsStream, NativePlacementDTO.class);
      BaseNativePlacementExtensionDTO actualDTO = nativePlacementDTO.getNativePlacementExtension();

      assertEquals(actualDTO.getClass(), expectedClass);
    }
  }
}
