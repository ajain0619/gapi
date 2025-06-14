package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.dto.seller.nativeads.NativeTestUtils.getNativeVideoAssetDTO;
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
class NativeVideoAssetDTOMapperTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new CustomObjectMapper();
  }

  @Test
  void shouldSerialize() throws IOException {
    NativeVideoAssetDTO nativeVideoAssetDTO = getNativeVideoAssetDTO();
    String serialized = objectMapper.writeValueAsString(nativeVideoAssetDTO);
    log.info("serialized [{}]", serialized);
    assertTrue(serialized.contains(nativeVideoAssetDTO.getKey()));
    assertTrue(serialized.contains(nativeVideoAssetDTO.getType().toString()));
    assertTrue(serialized.contains(nativeVideoAssetDTO.getVideo().getMaxDuration().toString()));
    assertTrue(serialized.contains(nativeVideoAssetDTO.getVideo().getMinDuration().toString()));
    nativeVideoAssetDTO
        .getVideo()
        .getProtocols()
        .forEach(protocol -> assertTrue(serialized.contains(protocol.toString())));
  }

  @Test
  void shouldDeserialize() throws IOException {
    String nativeDataAssetSTR =
        "        {\n"
            + "          \"key\": \"myVideo\",\n"
            + "          \"type\": \"VIDEO\",\n"
            + "          \"video\": {\n"
            + "            \"minDuration\": 10,\n"
            + "            \"maxDuration\": 25,\n"
            + "            \"protocols\": [\n"
            + "              \"VAST_3_0\",\n"
            + "              \"VAST_3_0_WRAPPER\"\n"
            + "            ]\n"
            + "          }\n"
            + "        }\n";
    NativeVideoAssetDTO value =
        objectMapper.readValue(nativeDataAssetSTR, NativeVideoAssetDTO.class);
    log.info("deserialized [{}]", value);
    assertEquals("myVideo", value.getKey());
    assertEquals("VIDEO", value.getType().toString());
    assertEquals("10", value.getVideo().getMinDuration().toString());
    assertEquals("25", value.getVideo().getMaxDuration().toString());

    assertTrue(value.getVideo().getProtocols().contains(VideoProtocols.VAST_3_0));
    assertTrue(value.getVideo().getProtocols().contains(VideoProtocols.VAST_3_0_WRAPPER));
  }

  @Test
  void shouldReturnCorrectPlaceholders() {
    NativeVideoAssetDTO nativeVideoAssetDTO = getNativeVideoAssetDTO();
    assertEquals("myVideo-value", nativeVideoAssetDTO.getMandatoryPlaceholder());
    assertThat(
        nativeVideoAssetDTO.getPlaceholders(),
        containsInAnyOrder(Arrays.asList("myVideo-value", "myVideo-linkUrl").toArray()));
  }
}
