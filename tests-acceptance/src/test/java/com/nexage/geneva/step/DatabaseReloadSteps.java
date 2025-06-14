package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.database.RestoreDatabaseUtils;
import io.cucumber.java.Before;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseReloadSteps {

  private final RestoreDatabaseUtils restoreDatabaseUtils;
  private final DatabaseUtils databaseUtils;

  public DatabaseReloadSteps(
      RestoreDatabaseUtils restoreDatabaseUtils, DatabaseUtils databaseUtils) {
    this.restoreDatabaseUtils = restoreDatabaseUtils;
    this.databaseUtils = databaseUtils;
  }

  @Before("@restoreCrudCoreDatabaseBefore")
  public void restore_crud_core_database() {
    log.info("Running @Before on @restoreCrudCoreDatabaseBefore tag");
    restoreDatabaseUtils.restoreCrudCoreDatabase();
  }

  @Before("@restoreCrudDWDatabaseBefore")
  public void restore_crud_dw_database() {
    log.info("Running @Before on @restoreCrudDWDatabaseBefore tag");
    restoreDatabaseUtils.restoreCrudDWDatabase();
  }

  @Before("@restoreGlobalCoreDatabasesBefore")
  public void restore_global_core_database() {
    log.info("Running @Before on @restoreGlobalCoreDatabasesBefore tag");
    restoreDatabaseUtils.restoreGlobalCoreDatabase();
  }

  @Before("@restoreGlobalDWDatabasesBefore")
  public void restore_global_dw_database() {
    log.info("Running @Before on @restoreGlobalDWDatabasesBefore tag");
    restoreDatabaseUtils.restoreGlobalDWDatabase();
  }

  @Before("@restoreTagArchiveVericaBefore")
  public void restore_tag_archive_vertica() {
    log.info("Running @Before on @restoreTagArchiveVericaBefore tag");
    databaseUtils.tagArchiveVertica();
  }
}
