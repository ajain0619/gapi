package com.nexage.app.web;

/** declare CRUD operations fot the crud entities */
public interface CrudController<DTOObject, DTOObjectIdentifier> {

  DTOObject create(DTOObject entity);

  DTOObject read(DTOObjectIdentifier entityIdentifier);

  DTOObject update(DTOObject entity, DTOObjectIdentifier dtoObjectIdentifier);

  void delete(DTOObjectIdentifier entityIdentifier);
}
