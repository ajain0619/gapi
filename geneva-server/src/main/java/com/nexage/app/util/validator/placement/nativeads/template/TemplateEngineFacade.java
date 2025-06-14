package com.nexage.app.util.validator.placement.nativeads.template;

import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.EMPTY_SET;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TemplateEngineFacade {
  private static final String OPENNING_BRACKET = "${";
  private static final String CLOSING_BRACKET = "}";
  private static final String ELSE_DIRECTIVE = "#else";

  private VelocityEngine engine;

  @Autowired
  public TemplateEngineFacade() {
    buildTemplateEngine();
  }

  /**
   * Reads a HTML template string, parse it, and return information about its placeholders.
   *
   * @param template The HTML template string to be processed.
   * @return a TemplateInfo object Each TemplateInfo object will hold following information:
   *     <p><b>notAllowedMarks</b> - a set of strings representing which forbidden template engine
   *     marks were found in the html template. Only #if and #end are allowed. #elseIf, for example,
   *     is not allowed.
   *     <p><b>nonConditionalPlaceholders</b> - a set of strings representing the non-conditional
   *     placeholders found, like [title-value, mainImage-sourceUrl]
   *     <p><b>placeholdersInsideConditionMap</b> - a map where:
   *     <p>Keys - the placeholders inside each #if() condition statement.
   *     <p>Values - the placeholders found between that #if() and the #end statements.
   * @see TemplateInfo
   */
  public TemplateInfo processTemplate(String template) {
    String templateName = prepareTemplate(template);
    List<String> allPlaceholders = new ArrayList<>();
    Map<String, List<String>> placeholdersInsideConditionsMap = new HashMap<>();
    List<String> nonConditionalPlaceholders = new ArrayList<>();

    try {
      Set<String> notAllowedMarks = getNotAllowedMarks(template);
      if (CollectionUtils.isEmpty(notAllowedMarks)) {
        Template engineTemplate = engine.getTemplate(templateName);
        SimpleNode simpleNode = (SimpleNode) engineTemplate.getData();
        BaseVisitor htmlTemplateVisitor =
            new HtmlTemplateVisitor(allPlaceholders, placeholdersInsideConditionsMap);
        simpleNode.jjtAccept(htmlTemplateVisitor, new Object());
        nonConditionalPlaceholders =
            getNonConditionalPlaceholders(allPlaceholders, placeholdersInsideConditionsMap);
      }
      return TemplateInfo.builder()
          .notAllowedMarks(notAllowedMarks)
          .nonConditionalPlaceholders(new HashSet<>(nonConditionalPlaceholders))
          .placeholdersInsideConditionMap(placeholdersInsideConditionsMap)
          .build();
    } catch (ResourceNotFoundException | ParseErrorException e) {
      log.error("failed to handle html template", e);
      return buildEmptyTemplateInfo();
    } finally {
      removeTemplateFromStringsRepository(templateName);
    }
  }

  void removeTemplateFromStringsRepository(String templateName) {
    StringResourceLoader.getRepository().removeStringResource(templateName);
  }

  void insertTemplateIntoStringsRepository(String template, String templateName) {
    StringResourceLoader.getRepository().putStringResource(templateName, template);
  }

  private TemplateInfo buildEmptyTemplateInfo() {
    return TemplateInfo.builder()
        .notAllowedMarks(EMPTY_SET)
        .nonConditionalPlaceholders(EMPTY_SET)
        .placeholdersInsideConditionMap(EMPTY_MAP)
        .build();
  }

  private String prepareTemplate(String template) {
    String templateName = "template-" + UUID.randomUUID().toString();
    insertTemplateIntoStringsRepository(template, templateName);
    return templateName;
  }

  private List<String> getNonConditionalPlaceholders(
      List<String> allPlaceholders, Map<String, List<String>> placeholdersInsideConditionsMap) {
    List<String> nonConditionalPlaceholders = new ArrayList<>();
    removeConditions(
        allPlaceholders, placeholdersInsideConditionsMap.keySet(), nonConditionalPlaceholders);
    removeConditionContent(placeholdersInsideConditionsMap, nonConditionalPlaceholders);
    return nonConditionalPlaceholders;
  }

  private void removeConditionContent(
      Map<String, List<String>> placeholdersInsideConditionsMap,
      List<String> nonConditionalPlaceholders) {
    List<String> allConditionsInternalPlaceholders =
        placeholdersInsideConditionsMap.values().stream()
            .flatMap(Collection::stream)
            .collect(toList());
    allConditionsInternalPlaceholders.forEach(nonConditionalPlaceholders::remove);
  }

  private void removeConditions(
      List<String> allPlaceholders,
      Set<String> placeholdersInsideConditions,
      List<String> nonConditionalPlaceholders) {
    nonConditionalPlaceholders.addAll(allPlaceholders);
    nonConditionalPlaceholders.removeAll(placeholdersInsideConditions);
  }

  private void buildTemplateEngine() {
    engine = new VelocityEngine();
    engine.setProperty(RESOURCE_LOADER, "string");
    engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
    engine.setProperty(RuntimeConstants.PARSER_HYPHEN_ALLOWED, "true");
    engine.init();
  }

  private Set<String> getNotAllowedMarks(String htmlTemplate) {
    Set<String> wrongMarks = new HashSet<>();
    if (htmlTemplate.contains(ELSE_DIRECTIVE)) {
      wrongMarks.add(ELSE_DIRECTIVE);
    }
    return wrongMarks;
  }

  public static class HtmlTemplateVisitor extends BaseVisitor {
    private final List<String> allPlaceholders;
    private final Map<String, List<String>> placeholdersInsideConditionsMap;

    public HtmlTemplateVisitor(
        List<String> allPlaceholders, Map<String, List<String>> placeholdersInsideConditionsMap) {
      this.allPlaceholders = allPlaceholders;
      this.placeholdersInsideConditionsMap = placeholdersInsideConditionsMap;
    }

    @Override
    public Object visit(ASTReference node, Object data) {
      var ifStmtNode = findIfStatementParent(node);
      if (ifStmtNode != null) {
        processIfStatement(ifStmtNode, node);
      }
      allPlaceholders.add(node.getRootString());
      return super.visit(node, data);
    }

    private ASTIfStatement findIfStatementParent(ASTReference node) {
      var parent = node.jjtGetParent();
      while (parent != null) {
        if (parent instanceof ASTIfStatement) {
          return (ASTIfStatement) parent;
        }
        parent = parent.jjtGetParent();
      }
      return null;
    }

    private void processIfStatement(ASTIfStatement astIfStatement, ASTReference astReference) {
      String cleanCondition = getPlaceholderInsideOfCondition(astIfStatement);
      String refLiteral =
          astReference.literal().replace(OPENNING_BRACKET, EMPTY).replace(CLOSING_BRACKET, EMPTY);
      if (placeholdersInsideConditionsMap.containsKey(cleanCondition)) {
        var placeholders = new ArrayList<>(placeholdersInsideConditionsMap.get(cleanCondition));
        placeholders.add(refLiteral);
        placeholdersInsideConditionsMap.put(cleanCondition, placeholders);
      } else {
        placeholdersInsideConditionsMap.put(cleanCondition, List.of());
      }
    }

    private String getPlaceholderInsideOfCondition(ASTIfStatement node) {
      String condition = node.jjtGetChild(0).jjtGetChild(0).literal();
      return removePlaceholderMarks(condition);
    }

    private String removePlaceholderMarks(String condition) {
      return condition.replace(OPENNING_BRACKET, EMPTY).replace(CLOSING_BRACKET, EMPTY);
    }
  }
}
