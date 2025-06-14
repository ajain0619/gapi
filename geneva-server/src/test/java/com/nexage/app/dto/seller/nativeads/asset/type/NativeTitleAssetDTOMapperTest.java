package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.dto.seller.nativeads.NativeTestUtils.getNativeTitleAssetDTO;
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
class NativeTitleAssetDTOMapperTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new CustomObjectMapper();
  }

  @Test
  void shouldSerialize() throws IOException {

    NativeTitleAssetDTO nativeTitleAssetDTO = getNativeTitleAssetDTO();

    String serialized = objectMapper.writeValueAsString(nativeTitleAssetDTO);
    log.info("serialized [{}]", serialized);
    assertTrue(serialized.contains(nativeTitleAssetDTO.getKey()));
    assertTrue(serialized.contains(nativeTitleAssetDTO.getType().toString()));
    assertTrue(serialized.contains(nativeTitleAssetDTO.getTitle().getMaxLength().toString()));
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
    NativeTitleAssetDTO nativeTitleAssetDTO = getNativeTitleAssetDTO();
    assertEquals("myTitle-value", nativeTitleAssetDTO.getMandatoryPlaceholder());
    assertThat(
        nativeTitleAssetDTO.getPlaceholders(),
        containsInAnyOrder(Arrays.asList("myTitle-value", "myTitle-linkUrl").toArray()));
  }
}
