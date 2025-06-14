package com.nexage.geneva.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GenevaCrudProperties {

  @Value("${crud.schema}")
  private String crudSchema;

  @Value("${crud.context}")
  private String crudContext;

  @Value("${crud.host}")
  private String crudHost;

  @Value("${crud.port}")
  private String crudPort;

  @Value("${crud.wm.port}")
  private String crudWmPort;

  @Value("${crud.wm.host}")
  private String crudWmHost;
}
