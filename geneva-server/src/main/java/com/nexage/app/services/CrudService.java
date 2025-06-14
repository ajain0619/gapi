package com.nexage.app.services;

/**
 * @param <DTOObject>
 * @param <DTOObjectIdentifier> TODO: create CRUD repository and add simple CRUD manager for storing
 *     data in the DB
 */
public interface CrudService<DTOObject, DTOObjectIdentifier> {

  DTOObject create(DTOObject dtoObject);

  DTOObject read(DTOObjectIdentifier dtoObjectIdentifier);

  DTOObject update(DTOObject dtoObject, DTOObjectIdentifier dtoObjectIdentifier);

  void delete(DTOObjectIdentifier dtoObjectIdentifier);
}
