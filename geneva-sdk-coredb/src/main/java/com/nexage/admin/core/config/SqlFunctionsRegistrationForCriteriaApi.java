package com.nexage.admin.core.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class SqlFunctionsRegistrationForCriteriaApi implements MetadataBuilderContributor {
  /**
   * apply custom function to make use mysql operator "REGEXP" to compare against pattern vs string
   *
   * @param metadataBuilder
   */
  @Override
  public void contribute(MetadataBuilder metadataBuilder) {
    metadataBuilder.applySqlFunction(
        "regexp", new SQLFunctionTemplate(StandardBasicTypes.BINARY, "?1 REGEXP ?2"));
  }
}
