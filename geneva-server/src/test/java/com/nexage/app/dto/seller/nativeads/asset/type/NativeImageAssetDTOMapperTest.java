package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.dto.seller.nativeads.NativeTestUtils.getNativeImageAssetDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
class NativeImageAssetDTOMapperTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new CustomObjectMapper();
  }

  @Test
  void shouldSerialize() throws IOException {
    NativeImageAssetDTO nativeImageAssetDTO = getNativeImageAssetDTO();
    String serialized = objectMapper.writeValueAsString(nativeImageAssetDTO);
    log.info("serialized [{}]", serialized);
    assertTrue(serialized.contains(nativeImageAssetDTO.getKey()));
    assertTrue(serialized.contains(nativeImageAssetDTO.getType().toString()));
    assertTrue(serialized.contains(nativeImageAssetDTO.getImage().getHeight().toString()));
    assertTrue(serialized.contains(nativeImageAssetDTO.getImage().getHeightMinimum().toString()));
    assertTrue(serialized.contains(nativeImageAssetDTO.getImage().getWidth().toString()));
    assertTrue(serialized.contains(nativeImageAssetDTO.getImage().getWidthMinimum().toString()));
  }

  @Test
  void shouldDeserialize() throws IOException {
    String nativeDataAssetSTR =
        "        {\n"
            + "          \"key\": \"myImage\",\n"
            + "          \"type\": \"IMAGE\",\n"
            + "          \"image\": {\n"
            + "            \"type\": \"MAIN\",\n"
            + "            \"width\": 200,\n"
            + "            \"widthMinimum\": 150,\n"
            + "            \"height\": 300,\n"
            + "            \"heightMinimum\": 280\n"
            + "          }\n"
            + "        }\n";
    NativeImageAssetDTO value =
        objectMapper.readValue(nativeDataAssetSTR, NativeImageAssetDTO.class);
    log.info(" deserialized [{}]", value);
    assertEquals("myImage", value.getKey());
    assertEquals("IMAGE", value.getType().toString());
    assertEquals("300", value.getImage().getHeight().toString());
    assertEquals("280", value.getImage().getHeightMinimum().toString());
    assertEquals("200", value.getImage().getWidth().toString());
    assertEquals("150", value.getImage().getWidthMinimum().toString());
  }

  @Test
  void shouldReturnCorrectPlaceholders() {
    NativeImageAssetDTO nativeImageAssetDTO = getNativeImageAssetDTO();
    assertEquals("myImage-sourceUrl", nativeImageAssetDTO.getMandatoryPlaceholder());
    assertThat(
        nativeImageAssetDTO.getPlaceholders(),
        containsInAnyOrder(Arrays.asList("myImage-sourceUrl", "myImage-linkUrl").toArray()));
  }
}
