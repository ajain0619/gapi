package com.ssp.geneva.common.attributeparsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssp.geneva.common.attributeparsing.dto.Group;
import com.ssp.geneva.common.attributeparsing.dto.OperatorType;
import com.ssp.geneva.common.attributeparsing.dto.TargetEntity;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;

public class AttributeParserTest {

  @Test
  public void shouldParseExpressionSuccessfully() {
    // given
    String exp =
        "(language IN [\"Arabic\", \"Dutch\"] AND content_series NOT IN [\"The Walking Dead\"] AND publisher_name IN [\"Yahoo Search\", \"Yahoo! NAR\"])";
    AttributeParser parser = new AttributeParser();
    List<Group> expectedGroupList = new ArrayList<>();
    List<TargetEntity> targetEntityList = new ArrayList<>();
    targetEntityList.add(
        setTargetEntityObject(OperatorType.IN, "language", List.of("Arabic", "Dutch")));
    targetEntityList.add(
        setTargetEntityObject(OperatorType.NOT_IN, "content_series", List.of("The Walking Dead")));
    targetEntityList.add(
        setTargetEntityObject(
            OperatorType.IN, "publisher_name", List.of("Yahoo Search", "Yahoo! NAR")));
    Group group = new Group();
    group.setTargets(targetEntityList);
    expectedGroupList.add(group);
    // when
    List<Group> actualGroupList = parser.parse(exp);
    // then
    assertEquals(expectedGroupList, actualGroupList);
  }

  @Test
  public void shouldParseExpressionSuccessfullyWithLessExp() {
    // given
    String exp =
        "(language IN [\"Arabic\", \"Dutch\"] AND content_series NOT IN [\"The Walking Dead\"])";
    AttributeParser parser = new AttributeParser();
    List<Group> expectedGroupList = new ArrayList<>();
    List<TargetEntity> targetEntityList = new ArrayList<>();
    targetEntityList.add(
        setTargetEntityObject(OperatorType.IN, "language", List.of("Arabic", "Dutch")));
    targetEntityList.add(
        setTargetEntityObject(OperatorType.NOT_IN, "content_series", List.of("The Walking Dead")));
    Group group = new Group();
    group.setTargets(targetEntityList);
    expectedGroupList.add(group);
    // when
    List<Group> actualGroupList = parser.parse(exp);
    // then
    assertEquals(expectedGroupList, actualGroupList);
  }

  @Test
  public void shouldParseExpressionSuccessWithMultipleGroups() {
    String exp =
        "(language IN [\"Arabic\", \"Dutch\"] AND content_series IN [\"The Walking Dead\"] AND publisher_name IN [\"Yahoo Search\", \"Yahoo! NAR\"]) OR (language IN [\"English\"] AND publisher_name IN [\"Aol News\", \"Microsoft-Bing!\"]) ";
    AttributeParser parser = new AttributeParser();
    List<Group> groupList = parser.parse(exp);
    assertFalse(groupList.isEmpty());
  }

  @Test
  public void shouldThrowParseExpressionError() {
    String exp = "LANGUAGE IN (\"Arabic\", \"Dutch\")";
    AttributeParser parser = new AttributeParser();
    assertThrows(ParseCancellationException.class, () -> parser.parse(exp));
  }

  private TargetEntity setTargetEntityObject(
      OperatorType operator, String attributeType, List<String> targetValues) {
    TargetEntity te = new TargetEntity();
    te.setOperatorType(operator);
    te.setAttributeType(attributeType);
    te.setTargetValues(targetValues);
    return te;
  }
}
