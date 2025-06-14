package com.nexage.app.services;

import com.nexage.admin.core.model.AppAlias;

public interface AppAliasService {

  /**
   * If an app alias entity exists return it, otherwise create a new one
   *
   * @param appAlias {@link String}
   * @return {@link AppAlias}
   */
  AppAlias findAppAlias(String appAlias);

  /**
   * Create new app alias entity
   *
   * @param appAlias {@link String}
   * @return {@link AppAlias}
   */
  AppAlias createAppAlias(String appAlias);
}
