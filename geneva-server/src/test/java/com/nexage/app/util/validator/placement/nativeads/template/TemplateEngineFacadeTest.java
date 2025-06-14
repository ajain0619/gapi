package com.nexage.app.util.validator.placement.nativeads.template;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.app.util.ResourceLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateEngineFacadeTest {

  public static String TEST_PLACEHOLDER = "TEST_PLACEHOLDER";
  private String htmlTemplate;

  @Spy private TemplateEngineFacade templateEngineFacade;

  private List<String> basicAllPlaceholders;

  @Captor ArgumentCaptor<String> templateNameInsert;

  @Captor ArgumentCaptor<String> templateNameClear;

  @BeforeEach
  public void init() throws IOException {
    htmlTemplate =
        IOUtils.toString(
            ResourceLoader.getResourceAsStream(
                "/data/nativeplacement/validate/template/testHtmlTemplate.vm"),
            UTF_8);
    basicAllPlaceholders =
        new ArrayList<>(
            Arrays.asList(
                "image_link_url",
                "title_link_url",
                "title",
                "icon_link_url",
                "icon",
                "sponsored_link_url",
                "sponsored",
                "ctatext",
                "ctatext_link_url",
                "price",
                "price_link_url",
                "image"));
  }

  @Test
  void testTemplateEngineWithRegularContent() {
    htmlTemplate = htmlTemplate.replace(TEST_PLACEHOLDER, StringUtils.EMPTY);
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(htmlTemplate);

    assertThat(
        templateInfo.getNonConditionalPlaceholders(),
        containsInAnyOrder(getBasicNonConditionalPlaceholders().toArray()));

    Map<String, List<String>> expectedConditions = getBasicConditionalPlaceholders();
    verifyConditionsContents(expectedConditions, templateInfo.getPlaceholdersInsideConditionMap());
    verifyInsertAndCleanTemplateFromRepo();
  }

  @Test
  void testTemplateEngineWithNonAllowedVelocityMark() {
    htmlTemplate = htmlTemplate.replace(TEST_PLACEHOLDER, "#if(acc)<aaa>#elseIf(false)<bbb>#end");
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(htmlTemplate);

    assertTrue(templateInfo.getNonConditionalPlaceholders().isEmpty());
    assertTrue(templateInfo.getPlaceholdersInsideConditionMap().isEmpty());
    assertThat(templateInfo.getNotAllowedMarks(), hasSize(1));
    verifyInsertAndCleanTemplateFromRepo();
  }

  @Test
  void testTemplateEngineWithPhonePlaceholderWithoutContentPlaceholder() {
    String phonePlaceholderWithoutContentPlaceholder =
        "    #if(${phone})\n" + "        <div>hello</div>\n" + "    #end\n";
    htmlTemplate =
        htmlTemplate.replace(TEST_PLACEHOLDER, phonePlaceholderWithoutContentPlaceholder);
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(htmlTemplate);

    basicAllPlaceholders.add("phone");
    assertThat(
        templateInfo.getNonConditionalPlaceholders(),
        containsInAnyOrder(getBasicNonConditionalPlaceholders().toArray()));

    Map<String, List<String>> expectedConditions = getBasicConditionalPlaceholders();
    expectedConditions.put("phone", List.of());
    verifyConditionsContents(expectedConditions, templateInfo.getPlaceholdersInsideConditionMap());
    verifyInsertAndCleanTemplateFromRepo();
  }

  @Test
  void testTemplateEngineWithPhonePlaceholderWithRedundantContentPlaceholder() {
    String phonePlaceholderWithRedundantContentPlaceholder =
        "    #if(${phone})\n"
            + "        <a href=\"${image_link_url}\">\n"
            + "            <div>${phone}</div>\n"
            + "        </a>\n"
            + "    #end\n";
    htmlTemplate =
        htmlTemplate.replace(TEST_PLACEHOLDER, phonePlaceholderWithRedundantContentPlaceholder);
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(htmlTemplate);

    basicAllPlaceholders.add("phone");
    List<String> expectedNonConditional = getBasicNonConditionalPlaceholders();
    assertThat(
        templateInfo.getNonConditionalPlaceholders(),
        containsInAnyOrder(expectedNonConditional.toArray()));

    Map<String, List<String>> expectedConditions = getBasicConditionalPlaceholders();
    expectedConditions.put("phone", List.of("image_link_url", "phone"));
    verifyConditionsContents(expectedConditions, templateInfo.getPlaceholdersInsideConditionMap());

    verifyInsertAndCleanTemplateFromRepo();
  }

  @Test
  void testTemplateEngineWhenParsingFails() {
    htmlTemplate = htmlTemplate.replace(TEST_PLACEHOLDER, "#if(abc)#if");
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(htmlTemplate);

    assertTrue(templateInfo.getNonConditionalPlaceholders().isEmpty());
    assertTrue(templateInfo.getPlaceholdersInsideConditionMap().isEmpty());
    assertTrue(templateInfo.getNotAllowedMarks().isEmpty());

    verifyInsertAndCleanTemplateFromRepo();
  }

  private void verifyInsertAndCleanTemplateFromRepo() {
    verify(templateEngineFacade, times(1))
        .insertTemplateIntoStringsRepository(anyString(), templateNameInsert.capture());

    verify(templateEngineFacade, times(1))
        .removeTemplateFromStringsRepository(templateNameClear.capture());
    assertEquals(templateNameInsert.getValue(), templateNameClear.getValue());
  }

  private Map<String, List<String>> getBasicConditionalPlaceholders() {
    Map<String, List<String>> expectedConditions = new HashMap<>();
    expectedConditions.put(
        "ctatext", List.of("ctatext_link_url", "ctatext", "ctatext_link_url", "ctatext"));
    expectedConditions.put("price", List.of("price", "price_link_url"));
    return expectedConditions;
  }

  private List<String> getBasicNonConditionalPlaceholders() {
    return new ArrayList<>(
        Arrays.asList(
            "image_link_url",
            "title_link_url",
            "title",
            "icon_link_url",
            "icon",
            "sponsored_link_url",
            "sponsored",
            "image"));
  }

  private void verifyConditionsContents(
      Map<String, List<String>> expectedConditions,
      Map<String, List<String>> placeholdersInsideConditionMap) {

    assertThat(
        expectedConditions.keySet(),
        containsInAnyOrder(placeholdersInsideConditionMap.keySet().toArray()));

    expectedConditions
        .keySet()
        .forEach(
            key -> {
              assertThat(
                  expectedConditions.get(key),
                  containsInAnyOrder(placeholdersInsideConditionMap.get(key).toArray()));
            });
  }
}
