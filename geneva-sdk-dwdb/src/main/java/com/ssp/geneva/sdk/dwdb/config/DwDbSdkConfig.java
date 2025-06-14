package com.ssp.geneva.sdk.dwdb.config;

import com.nexage.admin.dw.xstream.XmlReportDefinition;
import com.nexage.countryservice.CountryService;
import com.nexage.countryservice.CountryServiceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Log4j2
@Getter
@Setter
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.ssp.geneva.sdk.dwdb", "com.nexage.admin.dw", "com.nexage.dw"})
public class DwDbSdkConfig {

  public DwDbSdkConfig() {
    log.info("geneva.auto-config:DwDbSdkConfig");
  }

  @Bean("extendedDataSourceProperties")
  public Properties extendedDataSourceProperties(@Autowired Environment environment) {
    Properties properties = new Properties();
    if (environment.getProperty("characterEncoding") != null) {
      properties.setProperty("characterEncoding", environment.getProperty("characterEncoding"));
    }
    if (environment.getProperty("rewriteBatchedStatements") != null) {
      properties.setProperty(
          "rewriteBatchedStatements", environment.getProperty("rewriteBatchedStatements"));
    }
    if (environment.getProperty("preferredaddressfamily") != null) {
      properties.setProperty(
          "preferredaddressfamily", environment.getProperty("preferredaddressfamily"));
    }
    if (environment.getProperty("searchpath") != null) {
      properties.setProperty("searchpath", environment.getProperty("searchpath"));
    }
    return properties;
  }

  @Bean
  public DwDbSdkConfigProperties dwDbSdkConfigProperties(
      @Autowired Environment environment, @Autowired Properties extendedDataSourceProperties) {
    return DwDbSdkConfigProperties.builder()
        .poolName(environment.getProperty("dw.datasource.name"))
        .jdbcUrl(environment.getProperty("dw.datasource.url"))
        .driverClassName(environment.getProperty("dw.datasource.driver"))
        .maximumPoolSize(
            Integer.valueOf(environment.getProperty("dw.datasource.maxpoolsize", "10")))
        .idleTimeout(Long.valueOf(environment.getProperty("dw.datasource.idletimeout", "1000")))
        .connectionTimeout(
            Long.valueOf(environment.getProperty("dw.datasource.connectionTimeout", "30000")))
        .maxLifetime(Long.valueOf(environment.getProperty("dw.datasource.maxLifetime", "1800000")))
        .username(environment.getProperty("dw.datasource.username"))
        .password(environment.getProperty("dw.datasource.password"))
        .connectionTestQuery(environment.getProperty("dw.datasource.testquery"))
        .dataSourceProperties(extendedDataSourceProperties)
        .build();
  }

  @Bean("dwDbHikariConfig")
  public HikariConfig dwDbHikariConfig(@Autowired DwDbSdkConfigProperties dwDbSdkConfigProperties) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setPoolName(dwDbSdkConfigProperties.getPoolName());
    hikariConfig.setJdbcUrl(dwDbSdkConfigProperties.getJdbcUrl());
    hikariConfig.setDriverClassName(dwDbSdkConfigProperties.getDriverClassName());
    hikariConfig.setIdleTimeout(dwDbSdkConfigProperties.getIdleTimeout());
    hikariConfig.setConnectionTimeout(dwDbSdkConfigProperties.getConnectionTimeout());
    hikariConfig.setMaxLifetime(dwDbSdkConfigProperties.getMaxLifetime());
    hikariConfig.setUsername(dwDbSdkConfigProperties.getUsername());
    hikariConfig.setPassword(dwDbSdkConfigProperties.getPassword());
    hikariConfig.setConnectionTestQuery(dwDbSdkConfigProperties.getConnectionTestQuery());
    hikariConfig.setDataSourceProperties(dwDbSdkConfigProperties.getDataSourceProperties());
    return hikariConfig;
  }

  @Bean({"dwDS", "ssp.geneva.api.datasource.dw", "dwDataSource"})
  public DataSource dataSource(@Autowired HikariConfig dwDbHikariConfig) {
    return new HikariDataSource(dwDbHikariConfig);
  }

  @Bean("dwJdbcTemplate")
  public JdbcTemplate dwJdbcTemplate(@Autowired DataSource dwDS) {
    return new JdbcTemplate(dwDS);
  }

  @Bean("dwNamedJdbcTemplate")
  public NamedParameterJdbcTemplate dwNamedJdbcTemplate(@Autowired DataSource dwDS) {
    return new NamedParameterJdbcTemplate(dwDS);
  }

  @Bean("countryService")
  public CountryService countryService() {
    return CountryServiceFactory.create();
  }

  @Bean("marshaller")
  public XStreamMarshaller marshaller() {
    XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
    xStreamMarshaller.setAnnotatedClasses(XmlReportDefinition.class);
    return xStreamMarshaller;
  }

  @Bean("genevaReportFacadeList")
  public List<String> genevaReportFacadeList() {
    return List.of(
        "buyerReportFacadeImpl",
        "exchangeReportFacadeImpl",
        "financeReportFacadeImpl",
        "sellerReportFacadeImpl",
        "seatHolderReportFacadeImpl");
  }
}
