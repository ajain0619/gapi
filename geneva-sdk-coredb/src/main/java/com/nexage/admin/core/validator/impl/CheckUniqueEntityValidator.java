package com.nexage.admin.core.validator.impl;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.CheckUniqueEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CheckUniqueEntityValidator implements ConstraintValidator<CheckUniqueEntity, Object> {

  private String[] properties;
  private CoreDBErrorCodes errorCode;

  @PersistenceContext private EntityManager entityManager;

  @Override
  public void initialize(CheckUniqueEntity annotation) {
    properties = annotation.properties();
    setErrorCode(annotation.errorCode());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @PrePersist
  public boolean isValid(Object data, ConstraintValidatorContext annotationContext) {
    if (data == null) {
      return true;
    }
    Session session = (Session) entityManager.getDelegate();
    ClassMetadata metadata = session.getSessionFactory().getClassMetadata(data.getClass());

    DetachedCriteria criteria = DetachedCriteria.forClass(data.getClass());
    for (String field : properties) {
      criteria.add(Restrictions.eq(field, metadata.getPropertyValue(data, field)));
    }
    criteria.setProjection(Projections.rowCount());

    List<?> results = criteria.getExecutableCriteria(session).list();
    Number count = (Number) results.iterator().next();
    return count.intValue() == 0;
  }

  @Autowired
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setErrorCode(CoreDBErrorCodes errorCode) {
    this.errorCode = errorCode;
  }

  public CoreDBErrorCodes getErrorCode() {
    return errorCode;
  }
}
