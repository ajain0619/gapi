package com.nexage.app.dto.seller.nativeads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.Context;
import com.nexage.admin.core.enums.ContextSubType;
import com.nexage.admin.core.enums.PlacementType;
import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import com.nexage.app.dto.seller.nativeads.enums.DataType;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NativePlacementDTOSerializeTest {

  private static String NATIVE_PLACEMENT_FILE_LOCATION =
      "/data/nativeplacement/native_placement_extension_dto.json";
  private static String NATIVE_PLACEMENT_FILE_LOCATION_ALL_TYPES =
      "/data/nativeplacement/native_placement_extension_all_data_types.json";
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new CustomObjectMapper();
  }

  @Test
  void whenDeSerializingPolymorphic_verifySerializeDeserializeSameString() throws IOException {

    try (InputStream jsonInputStream =
        ResourceLoader.getResourceAsStream(NATIVE_PLACEMENT_FILE_LOCATION)) {
      NativePlacementExtensionDTO nativePlacementExtensionFromJson =
          objectMapper.readValue(jsonInputStream, NativePlacementExtensionDTO.class);

      assertEquals(
          ContextSubType.USER_GENERATED,
          nativePlacementExtensionFromJson.getContextSubType(),
          "ContextSubType, bad Serialize");
      assertEquals(
          Context.SOCIAL, nativePlacementExtensionFromJson.getContext(), "Context, bad Serialize");
      assertEquals(
          PlacementType.IN_FEED,
          nativePlacementExtensionFromJson.getPlacementType(),
          "PlacementType, bad Serialize");
      Set<NativeAssetSetDTO> assetSetsFromJson = nativePlacementExtensionFromJson.getAssetSets();
      assertTrue(
          assetSetsFromJson.contains(NativeTestUtils.getNativeDataAssetSetDTO()),
          "problem deserialize NativeDataAssetSetDTO");
      assertTrue(
          assetSetsFromJson.contains(NativeTestUtils.getNativeImageAssetSetDTO()),
          "problem deserialize NativeImageAssetSetDTO");

      assertTrue(
          assetSetsFromJson.contains(NativeTestUtils.getNativeVideoAssetSetDTO()),
          "problem deserialize NativeVideoAssetSetDTO");
      NativeAssetSetDTO nativeTitleAssetSetDTO = NativeTestUtils.getNativeTitleAssetSetDTO();
      assertTrue(
          assetSetsFromJson.contains(nativeTitleAssetSetDTO),
          "problem deserialize NativeTitleAssetSetDTO");
    }
  }

  @Test
  void whenSerializingDeSerializingPolymorphic_verifySameObject() throws IOException {

    NativePlacementExtensionDTO dto = new NativePlacementExtensionDTO();
    dto.setContext(Context.CONTENT);
    dto.setContextSubType(ContextSubType.APP_STORE);
    dto.setAssetTemplate("myBrilliantAssetTemplate");
    HashSet<NativeAssetSetDTO> assetSets = new HashSet<>();
    assetSets.add(NativeTestUtils.getNativeDataAssetSetDTO());
    assetSets.add(NativeTestUtils.getNativeImageAssetSetDTO());
    assetSets.add(NativeTestUtils.getNativeTitleAssetSetDTO());
    assetSets.add(NativeTestUtils.getNativeVideoAssetSetDTO());
    dto.setAssetSets(assetSets);

    String s = objectMapper.writeValueAsString(dto);
    NativePlacementExtensionDTO SerializedDeSerializedDto =
        objectMapper.readValue(s, NativePlacementExtensionDTO.class);

    assertEquals(SerializedDeSerializedDto, dto);
  }

  @Test
  void whenDeSerializingPolymorphic_AllDataTypesGetSerializedProperly() throws IOException {
    try (InputStream jsonInputStream =
        ResourceLoader.getResourceAsStream(NATIVE_PLACEMENT_FILE_LOCATION_ALL_TYPES)) {
      NativePlacementExtensionDTO nativePlacementExtensionFromJson =
          objectMapper.readValue(jsonInputStream, NativePlacementExtensionDTO.class);
      Set<NativeAssetDTO> allReqNoneAssets =
          nativePlacementExtensionFromJson.getAssetSets().stream()
              .filter(x -> x.getRule().equals(NativeAssetRule.REQ_NONE))
              .map(NativeAssetSetDTO::getAssets)
              .flatMap(Collection::stream)
              .collect(Collectors.toSet());
      assertEquals(allReqNoneAssets.size(), DataType.values().length);
    }
  }
}
