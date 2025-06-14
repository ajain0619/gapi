package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseSteps {

  @Autowired private DatabaseUtils databaseUtils;

  @Given("^delete from table \"(.+?)\" where column \"(.+?)\" has value \"(.+?)\"")
  public void deleteFromTableByColumnValue(String table, String column, String value) {
    databaseUtils.deleteCoreRecordsByFieldNameAndValue(table, column, value);
  }

  @Given("^set AUTO_INCREMENT for table \"(.+?)\" to \"([0-9]+)\"$")
  public void resetAutoIncrement(String table, String value) {
    databaseUtils.resetAutoIncrement(table, value);
  }
}
