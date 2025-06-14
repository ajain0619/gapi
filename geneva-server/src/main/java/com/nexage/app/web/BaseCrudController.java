package com.nexage.app.web;

import com.nexage.app.services.CrudService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Base CRUD controller */
public abstract class BaseCrudController<
        DTOObject,
        DTOObjectIdentifier,
        DTOObjectService extends CrudService<DTOObject, DTOObjectIdentifier>>
    implements CrudController<DTOObject, DTOObjectIdentifier> {

  protected abstract DTOObjectService getService();

  @Override
  @PostMapping
  @ResponseBody
  public DTOObject create(@RequestBody DTOObject dtoObject) {
    return getService().create(dtoObject);
  }

  @Override
  @GetMapping(value = "/{identifier}")
  @ResponseBody
  public DTOObject read(
      @PathVariable(value = "identifier") DTOObjectIdentifier dtoObjectIdentifier) {
    return getService().read(dtoObjectIdentifier);
  }

  @Override
  @PutMapping(value = "/{identifier}")
  @ResponseBody
  public DTOObject update(
      @RequestBody DTOObject dtoObject,
      @PathVariable(value = "identifier") DTOObjectIdentifier dtoObjectIdentifier) {
    return getService().update(dtoObject, dtoObjectIdentifier);
  }

  @Override
  @DeleteMapping(value = "/{identifier}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable(value = "identifier") DTOObjectIdentifier dtoObjectIdentifier) {
    getService().delete(dtoObjectIdentifier);
  }
}
