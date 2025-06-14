package com.ssp.geneva.common.metrics.config.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.MBeanServerFactoryBean;

@Configuration
public class JmxConfig {

  @Value("${ssp.geneva.jmx.default.domain:com.nexage.app}")
  private String defaultDomain;

  @Bean("mbeanServer")
  public MBeanServerFactoryBean mbeanServer() {
    MBeanServerFactoryBean mBeanServerFactoryBean = new MBeanServerFactoryBean();
    mBeanServerFactoryBean.setLocateExistingServerIfPossible(true);
    mBeanServerFactoryBean.setDefaultDomain(defaultDomain);
    return mBeanServerFactoryBean;
  }

  @Bean("mbeanExporter")
  @ConditionalOnClass({MBeanServerFactoryBean.class})
  @ConditionalOnBean(name = {"mbeanServer"})
  public AnnotationMBeanExporter mBeanExporter(@Autowired MBeanServerFactoryBean mbeanServer) {
    AnnotationMBeanExporter mbeanExporter = new AnnotationMBeanExporter();
    mbeanExporter.setServer(mbeanServer.getObject());
    mbeanExporter.setDefaultDomain(defaultDomain);
    return mbeanExporter;
  }
}
