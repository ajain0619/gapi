package com.nexage.app.dto.seller.nativeads;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class NativePlacementExtensionDTODeserializer
    extends JsonDeserializer<BaseNativePlacementExtensionDTO> {

  private static final String RENDERING_TEMPLATE = "renderingTemplate";

  @Override
  public BaseNativePlacementExtensionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    ObjectCodec codec = jp.getCodec();
    JsonNode node = codec.readTree(jp);
    if (node.has(RENDERING_TEMPLATE) && node.get(RENDERING_TEMPLATE) != null) {
      return codec.treeToValue(node, WebNativePlacementExtensionDTO.class);
    } else {
      return codec.treeToValue(node, NativePlacementExtensionDTO.class);
    }
  }
}
