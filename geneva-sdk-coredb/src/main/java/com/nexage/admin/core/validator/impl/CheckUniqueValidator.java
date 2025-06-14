package com.nexage.admin.core.validator.impl;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.CheckUnique;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CheckUniqueValidator implements ConstraintValidator<CheckUnique, String> {
  private Class<?> entity;
  private String fieldName;
  private CoreDBErrorCodes errorCode;

  @PersistenceContext private EntityManager entityManager;

  @Override
  public void initialize(CheckUnique annotation) {
    entity = annotation.entity();
    fieldName = annotation.fieldName();
    setErrorCode(annotation.errorCode());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @PrePersist
  public boolean isValid(String data, ConstraintValidatorContext annotationContext) {
    if (data == null) {
      return true;
    }

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery(entity);
    Root<?> root = criteriaQuery.from(entity);
    Path<String> attr = root.get(fieldName);
    criteriaQuery.where(criteriaBuilder.equal(attr, data));
    TypedQuery<?> query = entityManager.createQuery(criteriaQuery);
    boolean result = true;

    try {
      result = (query.getSingleResult() == null);
    } catch (NoResultException nre) {
    } catch (NonUniqueResultException e) {
      result = false;
    }
    return result;
  }

  /** @param entityManager the entityManager to set */
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /** @return the entityManager */
  public EntityManager getEntityManager() {
    return entityManager;
  }

  /** @param errorCode the errorCode to set */
  public void setErrorCode(CoreDBErrorCodes errorCode) {
    this.errorCode = errorCode;
  }

  /** @return the errorCode */
  public CoreDBErrorCodes getErrorCode() {
    return errorCode;
  }
}
