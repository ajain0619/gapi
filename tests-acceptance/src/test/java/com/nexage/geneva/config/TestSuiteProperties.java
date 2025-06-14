package com.nexage.geneva.config;

import com.nexage.geneva.config.properties.GenevaCrudProperties;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class TestSuiteProperties {

  private final GenevaCrudProperties genevaCrudProperties;

  private String schema;
  private String context;
  private String host;
  private String port;
  private String wmPort;
  private String wmHost;

  @Autowired
  public TestSuiteProperties(GenevaCrudProperties genevaCrudProperties) {
    this.genevaCrudProperties = genevaCrudProperties;
  }

  @PostConstruct
  public void init() {
    setTestSuiteProperties(
        genevaCrudProperties.getCrudSchema(),
        genevaCrudProperties.getCrudContext(),
        genevaCrudProperties.getCrudHost(),
        genevaCrudProperties.getCrudPort(),
        genevaCrudProperties.getCrudWmHost(),
        genevaCrudProperties.getCrudWmPort());
  }

  public void setTestSuiteProperties(
      String schema, String context, String host, String port, String wmHost, String wmPort) {
    this.context = context;
    this.host = host;
    this.port = port;
    this.schema = schema;
    this.wmHost = wmHost;
    this.wmPort = wmPort;
  }

  public String getContext() {
    return context;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public String getSchema() {
    return schema;
  }

  public String getWmPort() {
    return wmPort;
  }

  public String getWmHost() {
    return wmHost;
  }
}
