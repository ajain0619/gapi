package com.nexage.app.util.validator.placement.nativeads.decoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.validators.BaseTestWebNativeExtention;
import java.net.URLDecoder;
import java.net.URLEncoder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HtmlTemplateValidatorTest extends BaseTestWebNativeExtention {

  private HtmlTemplateValidator validator = new HtmlTemplateValidator();

  @SneakyThrows
  @Test
  void verifyInvalid_When_Number_Of_Open_And_Close_Tags_IsDifferent() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    String encodedHTML =
        URLDecoder.decode(webNativePlacementExtensionDTO.getRenderingTemplate(), UTF_8.toString());
    encodedHTML += ">>";
    webNativePlacementExtensionDTO.setRenderingTemplate(
        URLEncoder.encode(encodedHTML, UTF_8.toString()));

    boolean valid = validator.isValid(encodedHTML, context);

    assertFalse(valid);
  }

  @SneakyThrows
  @Test
  void verifyValid() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();
    String encodedHTML = webNativePlacementExtensionDTO.getRenderingTemplate();

    boolean valid = validator.isValid(encodedHTML, context);

    assertTrue(valid);
  }
}
