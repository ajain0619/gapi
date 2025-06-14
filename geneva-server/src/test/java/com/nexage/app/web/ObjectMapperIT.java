package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.security.FeatureVisibility;
import com.nexage.app.security.FeatureVisibility.MenuMixinForPublic;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class ObjectMapperIT {

  @Value("classpath:testFeatureVisibilty.json")
  private Resource menuResource;

  private FeatureVisibility menu;
  private String menuString;

  @Test
  void testMenuMapping() throws Exception {
    File f = menuResource.getFile();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    menu = mapper.readValue(f, new TypeReference<FeatureVisibility>() {});

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.addMixInAnnotations(FeatureVisibility.class, MenuMixinForPublic.class);

    menuString = mapper.writeValueAsString(menu);
    assertNotNull(menu);
  }

  @Test
  void testMenuWriter() throws Exception {
    File f = menuResource.getFile();

    ObjectMapper mapper = new ObjectMapper();
    FeatureVisibility features = mapper.readValue(f, new TypeReference<FeatureVisibility>() {});

    assertNotNull(features);
    assertNotNull(features.getLoginAccess(), "Should contain loginAccess at this point");

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.addMixInAnnotations(FeatureVisibility.class, MenuMixinForPublic.class);

    String menuString = mapper.writeValueAsString(features);

    assertNotNull(menuString);

    assertNotNull(menuString);
    assertTrue(!menuString.contains("loginAccess"), "Should not contain loginAccess");
  }

  @Test
  void testMenuWriterString() throws Exception {
    File f = menuResource.getFile();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    menu = mapper.readValue(f, new TypeReference<FeatureVisibility>() {});

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.addMixInAnnotations(FeatureVisibility.class, MenuMixinForPublic.class);

    menuString = mapper.writeValueAsString(menu);
    assertTrue(
        menuString.contains("\"hbPartners\":{\"visible\":true}"), "Should contain hbPartners");
    assertTrue(
        menuString.contains("\"experiments\":{\"visible\":true}"), "Should contain experiments");
  }
}
