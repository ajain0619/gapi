package com.nexage.app.config;

import com.google.common.base.Strings;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.AbstractEnvironment;

/**
 * This class is used only in MTC, this class replaces DatabaseConfig
 *
 * @author octavio.martin.montenegro@oath.com
 */
@Log4j2
@Deprecated
public class SpringProfileListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String profiles = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
    log.info("Reading active spring profile property: {}", profiles);
    if (Strings.isNullOrEmpty(profiles)) {
      System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "default");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // no-op
  }
}
