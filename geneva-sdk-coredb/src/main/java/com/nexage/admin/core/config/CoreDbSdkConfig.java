package com.nexage.admin.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import javax.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Log4j2
@Getter
@Setter
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.nexage.admin.core"})
@EnableJpaRepositories(
    basePackages = {"com.nexage.admin.core.repository"},
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = {".*[Dao]"}))
public class CoreDbSdkConfig {

  public CoreDbSdkConfig() {
    log.info("geneva.auto-config:CoreDbSdkConfig");
  }

  @Bean
  public CoreDbSdkConfigProperties coreDbSdkConfigProperties(@Autowired Environment environment) {
    return CoreDbSdkConfigProperties.builder()
        .poolName(environment.getProperty("core.datasource.name"))
        .jdbcUrl(environment.getProperty("core.datasource.url"))
        .driverClassName(environment.getProperty("core.datasource.driver"))
        .maximumPoolSize(
            Integer.valueOf(environment.getProperty("core.datasource.maxpoolsize", "10")))
        .idleTimeout(Long.valueOf(environment.getProperty("core.datasource.idletimeout", "1000")))
        .connectionTimeout(
            Long.valueOf(environment.getProperty("core.datasource.connectionTimeout", "30000")))
        .maxLifetime(
            Long.valueOf(environment.getProperty("core.datasource.maxLifetime", "1800000")))
        .username(environment.getProperty("core.datasource.username"))
        .password(environment.getProperty("core.datasource.password"))
        .connectionTestQuery(environment.getProperty("core.datasource.testquery"))
        .build();
  }

  @Bean({"validator", "beanValidator", "localValidatorFactoryBean"})
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  @Lazy
  public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(final Validator validator) {
    return hibernateProperties ->
        hibernateProperties.putAll(
            Map.of(
                "javax.persistence.validation.factory",
                validator,
                "javax.persistence.validation.group.pre-persist",
                "javax.validation.groups.Default,com.nexage.admin.core.validator.CheckUniqueGroup,com.nexage.admin.core.validator.CreateGroup",
                "javax.persistence.validation.group.pre-update",
                "javax.validation.groups.Default,com.nexage.admin.core.validator.UpdateGroup"));
  }

  @Bean("jpaAdapter")
  public JpaVendorAdapter jpaAdapter() {
    var vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(false);
    vendorAdapter.setShowSql(false);
    return vendorAdapter;
  }

  @Bean("coreDbHikariConfig")
  public HikariConfig coreDbHikariConfig(
      @Autowired CoreDbSdkConfigProperties coreDbSdkConfigProperties) {
    var hikariConfig = new HikariConfig();
    hikariConfig.setPoolName(coreDbSdkConfigProperties.getPoolName());
    hikariConfig.setJdbcUrl(coreDbSdkConfigProperties.getJdbcUrl());
    hikariConfig.setDriverClassName(coreDbSdkConfigProperties.getDriverClassName());
    hikariConfig.setIdleTimeout(coreDbSdkConfigProperties.getIdleTimeout());
    hikariConfig.setConnectionTimeout(coreDbSdkConfigProperties.getConnectionTimeout());
    hikariConfig.setMaxLifetime(coreDbSdkConfigProperties.getMaxLifetime());
    hikariConfig.setUsername(coreDbSdkConfigProperties.getUsername());
    hikariConfig.setPassword(coreDbSdkConfigProperties.getPassword());
    hikariConfig.setConnectionTestQuery(coreDbSdkConfigProperties.getConnectionTestQuery());
    return hikariConfig;
  }

  @Bean({"coreDS", "ssp.geneva.api.datasource.core", "dataSource"})
  public DataSource dataSource(@Autowired HikariConfig coreDbHikariConfig) {
    return new HikariDataSource(coreDbHikariConfig);
  }

  @Bean("coreJdbcTemplate")
  public JdbcTemplate coreJdbcTemplate(@Autowired DataSource coreDS) {
    return new JdbcTemplate(coreDS);
  }

  @Bean("coreServicesJdbcTemplate")
  public JdbcTemplate coreServicesJdbcTemplate(@Autowired DataSource coreDS) {
    return new JdbcTemplate(coreDS);
  }

  @Bean("jpaDialect")
  public HibernateJpaDialect jpaDialect() {
    return new HibernateJpaDialect();
  }

  @Bean("entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Autowired DataSource coreDS) {
    var entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(coreDS);
    entityManagerFactory.setPackagesToScan("com.nexage.admin.core");
    entityManagerFactory.setJpaVendorAdapter(jpaAdapter());
    entityManagerFactory.setJpaDialect(jpaDialect());
    entityManagerFactory.setPersistenceUnitName("ApplicationEntityManager");
    entityManagerFactory.setJpaProperties(getProperties());
    return entityManagerFactory;
  }

  @Bean("transactionManager")
  public PlatformTransactionManager transactionManager(
      @Autowired LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    var transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    transactionManager.setNestedTransactionAllowed(true);

    return transactionManager;
  }

  @Bean("exceptionTranslation")
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean("persistenceAnnotationBeanPostProcessor")
  public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
    return new PersistenceAnnotationBeanPostProcessor();
  }

  @Bean("txTemplate")
  public TransactionTemplate transactionTemplate(
      @Autowired PlatformTransactionManager transactionManager) {
    var transactionTemplate = new TransactionTemplate();
    transactionTemplate.setTransactionManager(transactionManager);
    transactionTemplate.setPropagationBehavior(3);
    return transactionTemplate;
  }

  private Properties getProperties() {
    var properties = new Properties();
    properties.putAll(Map.of("javax.persistence.validation.factory", validator()));
    return properties;
  }
}
