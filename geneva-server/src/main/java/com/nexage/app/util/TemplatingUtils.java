package com.nexage.app.util;

import java.io.StringWriter;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.RenderTool;

@Log4j2
public final class TemplatingUtils {

  private TemplatingUtils() {}

  private static final int DEFAULT_NUMBER_OF_PARSERS = 40;

  static {
    try {
      Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
      Velocity.setProperty(
          "class.resource.loader.class",
          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      String p = System.getProperty("parser.pool.size");

      int parsers = DEFAULT_NUMBER_OF_PARSERS;
      if (StringUtils.isNotBlank(p)) {
        try {
          parsers = Integer.parseInt(p);
        } catch (NumberFormatException e) {
          log.error(
              "system property 'parser.pool.size' is not a valid number. parsers defaulting to: {}",
              parsers);
        }
      }
      Velocity.setProperty(RuntimeConstants.PARSER_POOL_SIZE, parsers);
      log.info(
          "velocity parser pool size: {}", Velocity.getProperty(RuntimeConstants.PARSER_POOL_SIZE));

      Velocity.init();
    } catch (Exception e) {
      log.error("ERROR : Exception in static initializer", e);
    }
  }

  // Wrap creating the context with appropriate tools. Hack for now.
  private static final MathTool mathTool = new MathTool();
  private static final RenderTool renderTool = new RenderTool();
  private static final HashUtils hash = new HashUtils();

  private static VelocityContext getVelocityContext(Map<String, String> customContext) {
    VelocityContext context = new VelocityContext();
    // Add any required tools here.
    context.put("math", mathTool);
    context.put("renderTool", renderTool);
    context.put("hash", hash);
    customContext.keySet().forEach(key -> context.put(key, customContext.get(key)));
    // contain itself.
    context.put("context", context);
    return context;
  }

  /**
   * Replace tokens in the input pattern, using the Map of values and return the String.
   *
   * @param pattern input pattern
   * @param values map of values
   */
  public static String createFromString(String pattern, Map<String, String> values)
      throws Exception {
    StringWriter s = new StringWriter();
    log.debug("Input String : {}", pattern);
    VelocityContext context = getVelocityContext(values);
    Velocity.evaluate(context, s, "test", pattern);
    String retVal = s.toString();
    log.debug("output string : {}", retVal);
    return retVal;
  }
}
