package com.nexage.admin.dw.test.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/*
 * A helper class until spring-test is upgraded to the 3.2.X package
 *
 *
 */
@Log4j2
public class JdbcHelperUtils {

  public static void executeSqlScript(
      JdbcTemplate jdbcTemplate, Resource resource, Boolean continueOnError)
      throws DataAccessException {
    executeSqlScript(jdbcTemplate, new EncodedResource(resource), continueOnError);
  }

  public static void executeSqlScript(
      JdbcTemplate jdbcTemplate, EncodedResource resource, boolean continueOnError)
      throws DataAccessException {

    if (log.isInfoEnabled()) {
      log.info("Executing SQL script from " + resource);
    }

    long startTime = System.currentTimeMillis();
    List<String> statements = new LinkedList<>();
    LineNumberReader reader = null;
    try {
      reader = new LineNumberReader(resource.getReader());
      String script = ScriptUtils.readScript(reader, "", null, null);
      char delimiter = ';';
      if (!ScriptUtils.containsSqlScriptDelimiters(script, Character.toString(delimiter))) {
        delimiter = '\n';
      }
      ScriptUtils.splitSqlScript(script, delimiter, statements);
      for (String statement : statements) {
        try {
          int rowsAffected = jdbcTemplate.update(statement);
          if (log.isDebugEnabled()) {
            log.debug(rowsAffected + " rows affected by SQL: " + statement);
          }
        } catch (DataAccessException ex) {
          if (continueOnError) {
            if (log.isWarnEnabled()) {
              log.warn("SQL: " + statement + " failed", ex);
            }
          } else {
            throw ex;
          }
        }
      }
      long elapsedTime = System.currentTimeMillis() - startTime;
      if (log.isInfoEnabled()) {
        log.info(
            "Done executing SQL scriptBuilder from " + resource + " in " + elapsedTime + " ms.");
      }
    } catch (IOException ex) {
      throw new DataAccessResourceFailureException(
          "Failed to open SQL script from " + resource, ex);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        // ignore
      }
    }
  }
}
