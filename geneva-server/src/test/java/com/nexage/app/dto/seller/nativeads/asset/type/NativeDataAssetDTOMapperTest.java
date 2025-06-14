package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.dto.seller.nativeads.NativeTestUtils.getNativeDataAssetDTO;
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
class NativeDataAssetDTOMapperTest {

  ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new CustomObjectMapper();
  }

  @Test
  void shouldSerialize() throws IOException {
    NativeDataAssetDTO nativeDataAssetDTO = getNativeDataAssetDTO();
    String serialized = objectMapper.writeValueAsString(nativeDataAssetDTO);
    log.info("serialized [{}]", serialized);
    assertTrue(serialized.contains(nativeDataAssetDTO.getKey()));
    assertTrue(serialized.contains(nativeDataAssetDTO.getType().toString()));
    assertTrue(serialized.contains(nativeDataAssetDTO.getData().getMaxLength().toString()));
    assertTrue(serialized.contains(nativeDataAssetDTO.getData().getType().toString()));
  }

  @Test
  void shouldDeserialize() throws IOException {

    String nativeDataAssetSTR =
        "        {\n"
            + "          \"key\": \"myTitle\",\n"
            + "          \"type\": \"TITLE\",\n"
            + "          \"title\": {\n"
            + "            \"maxLength\": 75\n"
            + "          }\n"
            + "        }\n";
    NativeTitleAssetDTO value =
        objectMapper.readValue(nativeDataAssetSTR, NativeTitleAssetDTO.class);
    log.info(" deserialized [{}]", value);

    assertEquals("myTitle", value.getKey());
    assertEquals("TITLE", value.getType().toString());
    assertEquals("75", value.getTitle().getMaxLength().toString());
  }

  @Test
  void shouldReturnCorrectPlaceholders() {
    NativeDataAssetDTO nativeDataAssetDTO = getNativeDataAssetDTO();
    assertEquals("mySalesPriceData-value", nativeDataAssetDTO.getMandatoryPlaceholder());
    assertThat(
        nativeDataAssetDTO.getPlaceholders(),
        containsInAnyOrder(
            Arrays.asList("mySalesPriceData-value", "mySalesPriceData-linkUrl").toArray()));
  }
}
