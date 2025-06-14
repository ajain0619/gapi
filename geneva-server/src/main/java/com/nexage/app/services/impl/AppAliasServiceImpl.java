package com.nexage.app.services.impl;

import static java.util.Objects.isNull;

import com.nexage.admin.core.model.AppAlias;
import com.nexage.admin.core.repository.AppAliasRepository;
import com.nexage.app.services.AppAliasService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@Log4j2
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class AppAliasServiceImpl implements AppAliasService {

  private final AppAliasRepository appAliasRepository;

  @Autowired
  public AppAliasServiceImpl(AppAliasRepository appAliasRepository) {
    this.appAliasRepository = appAliasRepository;
  }

  @Override
  public AppAlias findAppAlias(String appAlias) {
    AppAlias app = appAliasRepository.findByAppAlias(appAlias);
    if (isNull(app)) {
      return createAppAlias(appAlias);
    }
    return app;
  }

  @Override
  public AppAlias createAppAlias(String appAlias) {
    AppAlias app = new AppAlias();
    app.setAppAlias(appAlias);
    return appAliasRepository.save(app);
  }
}
