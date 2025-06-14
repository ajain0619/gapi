package com.nexage.geneva.database;

import com.nexage.geneva.util.TestUtils;
import java.sql.Connection;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class RestoreDatabaseUtils {

  @Autowired
  @Qualifier("dbCoreJdbcTemplate")
  private JdbcTemplate jdbcTemplateDbCore;

  @Autowired
  @Qualifier("crudDatawarehouseJdbcTemplate")
  private JdbcTemplate jdbcTemplateCrudDw;

  @Autowired
  @Value("${db.core.file}")
  private String dbCoreFile;

  @Autowired
  @Value("${crud.dw.schema.file}")
  private String crudDwSchemaFile;

  @Autowired
  @Value("${crud.dw.data.file}")
  private String crudDwDataFile;

  @Autowired
  @Value("${db.core.global.core.file}")
  private String globalCoreFile;

  @Autowired
  @Value("${crud.global.dw.schema.file}")
  private String globalDwSchemaFile;

  @Autowired
  @Value("${crud.global.dw.data.file}")
  private String globalDwDataFile;

  private static final String DBCORE = "db core";
  private static final String CRUDDWSCHEMA = "crud dw schema";
  private static final String CRUDDWDATA = "crud dw data";

  private static final String DBCOREGLOBALCORE = "db core global core";
  private static final String GLOBALDWSCHEMA = "global dw schema";
  private static final String GLOBALDWDATA = "global dw data";

  private static final boolean continueOnError = false;
  private static final boolean ignoreFailedDrops = false;
  private static final String commentPrefix = "--";
  private static final String separator = ";";
  private static final String blockCommentStartDelimiter = "--";
  private static final String blockCommentEndDelimiter = "--";
  private static final int UNEXPECTED_ERROR = -1;

  public void restoreCrudCoreDatabase() {
    restore(jdbcTemplateDbCore, dbCoreFile, DBCORE);
  }

  public void restoreCrudDWDatabase() {
    restore(jdbcTemplateCrudDw, crudDwSchemaFile, CRUDDWSCHEMA);
    restore(jdbcTemplateCrudDw, crudDwDataFile, CRUDDWDATA);
  }

  public void restoreGlobalCoreDatabase() {
    restore(jdbcTemplateDbCore, globalCoreFile, DBCOREGLOBALCORE);
  }

  public void restoreGlobalDWDatabase() {
    restore(jdbcTemplateCrudDw, globalDwSchemaFile, GLOBALDWSCHEMA);
    restore(jdbcTemplateCrudDw, globalDwDataFile, GLOBALDWDATA);
  }

  private void restore(JdbcTemplate jdbcTemplate, String dumpPath, String databaseName) {
    try {
      Connection connection = jdbcTemplate.getDataSource().getConnection();
      ScriptUtils.executeSqlScript(
          connection,
          new EncodedResource(new PathResource(dumpPath)),
          continueOnError,
          ignoreFailedDrops,
          commentPrefix,
          separator,
          blockCommentStartDelimiter,
          blockCommentEndDelimiter);
      connection.close();
      log.info("Restoring " + databaseName + " is completed successfully!");
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
      TestUtils.terminateTests(UNEXPECTED_ERROR);
    }
  }
}
