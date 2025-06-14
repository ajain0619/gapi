package com.ssp.geneva.common.attributeparsing;

import com.ssp.geneva.common.attributeparsing.dto.Group;
import com.ssp.geneva.common.attributeparsing.dto.OperatorType;
import com.ssp.geneva.common.attributeparsing.dto.TargetEntity;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;

/*** This is a parser which takes target expression as input and parse it using defined grammer */
@Component
public class AttributeParser extends ExpBaseListener {
  private static List<Group> groupList;
  private TargetEntity targetEntity;
  private Group group;
  private List<TargetEntity> targetEntities;

  @Override
  public void exitDef(ExpParser.DefContext ctx) {
    if (ctx.getChild(ExpParser.OR) != null) return;
    if (ctx.getParent() == null || ctx.getParent().getChild(ExpParser.OR) != null) {
      group.setTargets(targetEntities);
      groupList.add(group);
    }

    super.exitDef(ctx);
  }

  @Override
  public void exitValues(ExpParser.ValuesContext ctx) {
    targetEntity.setTargetValues(
        ctx.VALUE().stream().map(value -> value.getText().replace("\"", "")).toList());
    super.exitValues(ctx);
    targetEntities.add(targetEntity);
  }

  @Override
  public void exitOperator(ExpParser.OperatorContext ctx) {
    targetEntity.setOperatorType(OperatorType.of(ctx.getText()));
    super.exitOperator(ctx);
  }

  @Override
  public void exitKey(ExpParser.KeyContext ctx) {
    targetEntity = new TargetEntity();
    ctx.getParent();
    targetEntity.setAttributeType(ctx.ID().getText());
    super.exitKey(ctx);
  }

  @Override
  public void enterDef(ExpParser.DefContext ctx) {
    group = new Group();
    targetEntities = new ArrayList<>();
    super.enterDef(ctx);
  }

  /***
   * Parse incoming target expression into List<Group> using defined grammar
   * @param expression
   * @return List<Group>
   */
  public List<Group> parse(String expression) {
    groupList = new ArrayList<>();
    ANTLRInputStream in = new ANTLRInputStream(expression);
    ExpLexer lexer = new ExpLexer(in);
    lexer.removeErrorListeners();
    lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpParser parser = new ExpParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(ThrowingErrorListener.INSTANCE);
    parser.setBuildParseTree(true); // tell ANTLR to build a parse tree
    ParseTree tree = parser.def();

    /** Create standard walker. */
    ParseTreeWalker walker = new ParseTreeWalker();
    AttributeParser mapper = new AttributeParser();
    walker.walk(mapper, tree);

    return groupList;
  }
}
