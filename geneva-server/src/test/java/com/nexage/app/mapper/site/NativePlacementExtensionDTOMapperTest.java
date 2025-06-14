package com.nexage.app.mapper.site;

import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ALL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.nexage.app.dto.seller.nativeads.BaseNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeTitleAssetDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NativePlacementExtensionDTOMapperTest {

  private static final String NATIVE_POSITION_AS_STRING =
      "{\n"
          + "  \"pid\": 1,\n"
          + "  \"version\": 2,\n"
          + "  \"sitePid\": 220364,\n"
          + "  \"screenLocation\": \"FOOTER_VISIBLE\",\n"
          + "  \"mraidSupport\": \"YES\",\n"
          + "  \"mraidAdvancedTracking\": false,\n"
          + "  \"positionAliasName\": \"test1\",\n"
          + "  \"memo\": \"test1\",\n"
          + "  \"name\": \"test1\",\n"
          + "  \"videoSupport\": \"NATIVE\",\n"
          + "  \"placementCategory\": \"NATIVE_V2\",\n"
          + "  \"nativePlacementExtension\": {\n"
          + "    \"context\": \"SOCIAL\",\n"
          + "    \"contextSubType\": \"USER_GENERATED\",\n"
          + "    \"placementType\": \"IN_FEED\",\n"
          + "    \"assetTemplate\": \"awesomeAssets\",\n"
          + "    \"assetSets\": [\n"
          + "      {\n"
          + "        \"rule\": \"REQ_ALL\",\n"
          + "        \"assets\": [\n"
          + "          {\n"
          + "            \"key\": \"myTitle\",\n"
          + "            \"type\": \"TITLE\",\n"
          + "            \"title\": {\n"
          + "              \"maxLength\": 75\n"
          + "            }\n"
          + "          }\n"
          + "        ]\n"
          + "      }\n"
          + "    ]\n"
          + "  }\n"
          + "}\n";
  private static final String NATIVE_EXT =
      "\n{"
          + "    \"context\": \"SOCIAL\",\n"
          + "    \"contextSubType\": \"USER_GENERATED\",\n"
          + "    \"placementType\": \"IN_FEED\",\n"
          + "    \"assetTemplate\": \"awesomeAssets\",\n"
          + "    \"assetSets\": [\n"
          + "      {\n"
          + "        \"rule\": \"REQ_ALL\",\n"
          + "        \"assets\": [\n"
          + "          {\n"
          + "            \"key\": \"myTitle\",\n"
          + "            \"type\": \"TITLE\",\n"
          + "            \"title\": {\n"
          + "              \"maxLength\": 75\n"
          + "            }\n"
          + "          }\n"
          + "        ]\n"
          + "      }\n"
          + "    ]\n"
          + "  }\n";

  private CustomObjectMapper objectMapper;
  private final NativePlacementExtensionDTOMapper nativePlacementExtensionDTOMapper =
      new NativePlacementExtensionDTOMapper(mock(CustomObjectMapper.class));

  @BeforeEach
  void setup() {
    objectMapper = new CustomObjectMapper();
    ReflectionTestUtils.setField(nativePlacementExtensionDTOMapper, "objectMapper", objectMapper);
  }

  @Test
  void convertToNativePlacementExtensionDto() {

    final BaseNativePlacementExtensionDTO nativePlacementExtensionDto =
        nativePlacementExtensionDTOMapper.convertToNativePlacementExtensionDto(NATIVE_EXT);

    assertEquals("awesomeAssets", nativePlacementExtensionDto.getAssetTemplate());
    assertEquals(NativePlacementExtensionDTO.class, nativePlacementExtensionDto.getClass());
    NativeAssetSetDTO assetSetDTO =
        (NativeAssetSetDTO) nativePlacementExtensionDto.getAssetSets().toArray()[0];
    assertEquals(REQ_ALL.name(), assetSetDTO.getRule().toString());

    final NativeTitleAssetDTO title = (NativeTitleAssetDTO) assetSetDTO.getAssets().toArray()[0];

    assertEquals("myTitle", title.getKey());
    assertEquals("75", title.getTitle().getMaxLength().toString());
  }

  @Test
  void convertToPosition() throws IOException {
    final NativePlacementDTO source =
        objectMapper.readValue(NATIVE_POSITION_AS_STRING, NativePlacementDTO.class);

    final BaseNativePlacementExtensionDTO dto = source.getNativePlacementExtension();
    final String nativeConfigAsStr = nativePlacementExtensionDTOMapper.convertToPosition(dto);

    assertTrue(nativeConfigAsStr.contains(source.getNativePlacementExtension().getAssetTemplate()));
    NativeAssetSetDTO assetSetDTO =
        (NativeAssetSetDTO) source.getNativePlacementExtension().getAssetSets().toArray()[0];
    assertTrue(nativeConfigAsStr.contains(assetSetDTO.getRule().toString()));
    NativeTitleAssetDTO title = (NativeTitleAssetDTO) assetSetDTO.getAssets().toArray()[0];
    assertTrue(nativeConfigAsStr.contains(title.getKey()));
    assertTrue(nativeConfigAsStr.contains(title.getTitle().getMaxLength().toString()));

    assertTrue(nativeConfigAsStr.contains(dto.getAssetTemplate()));
  }
}
