package com.ssp.geneva.common.base.settings;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * This interface defines a common expected functionality for settings configuration independently
 * of the source.
 */
public interface SystemConfigurable {
  Map<String, Object> parameters = new HashMap<>();

  default Object getParameter(String parameter) {
    return parameters.get(parameter);
  }

  /**
   * This entrypoint is meant to be overridden to accept the reset of the parameters. This is here
   * for backward compatibility of previous implementation after common refactoring.
   */
  void restart();

  /**
   * This definition collects expected default predefined entities, so any configurable mechanism
   * must use them, to keep backward compatibility.
   */
  @Getter
  enum PredefinedParameterKey {
    ERROR_TRACE_ENABLED_KEY("geneva.errortrace.enable"),
    SSO_B2B_TOKEN_REFRESH_INTERVAL_KEY("sso.b2b.token.refresh.interval");

    String parameterKey;

    PredefinedParameterKey(String parameterKey) {
      this.parameterKey = parameterKey;
    }
  }
}
