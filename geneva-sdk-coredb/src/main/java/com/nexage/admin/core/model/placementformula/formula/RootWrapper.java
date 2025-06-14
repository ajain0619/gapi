package com.nexage.admin.core.model.placementformula.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

public class RootWrapper<R> {
  private Root<R> root;
  private AbstractQuery<R> rootQuery;
  private Map<String, Path<?>> pathCache = new HashMap<>();

  public RootWrapper(Root<R> root, AbstractQuery<R> rootQuery) {
    this.root = root;
    this.rootQuery = rootQuery;
  }

  public Root<R> getRoot() {
    return root;
  }

  public AbstractQuery<R> getRootQuery() {
    return rootQuery;
  }

  public <T> Path<T> getPath(String... pathParts) {
    return getPath(StringUtils.join(pathParts, '.'));
  }

  public <T> Path<T> getPath(String path) {
    Path<T> result = getFromCache(path);

    if (result != null) {
      return result;
    }

    List<String> elapsedParts = new ArrayList<>();
    for (String currentPart : path.split("\\.")) {
      elapsedParts.add(currentPart);
      String elapsedPath = StringUtils.join(elapsedParts, '.');
      if (result == null) {
        result = root.get(currentPart);
        if (doesNeedJoin(result)) {
          result = getFromCache(elapsedPath);
          if (result == null) {
            result = root.join(currentPart, JoinType.LEFT);
            putToCache(elapsedPath, result);
          }
        }
      } else {
        Path<T> tmpResult = result.get(currentPart);
        if (doesNeedJoin(tmpResult)) {
          tmpResult = getFromCache(elapsedPath);
          if (tmpResult == null) {
            result = ((Join<Object, T>) result).join(currentPart, JoinType.LEFT);
            putToCache(elapsedPath, result);
          } else {
            result = tmpResult;
          }
        } else {
          result = tmpResult;
          putToCache(elapsedPath, result);
        }
      }
    }
    return result;
  }

  private <T> Path<T> getFromCache(String path) {
    return (Path<T>) pathCache.get(path);
  }

  private void putToCache(String path, Path<?> join) {
    pathCache.put(path, join);
  }

  private boolean doesNeedJoin(final Path<?> result) {
    return result instanceof PluralAttributePath
        || ((SingularAttributePath) result)
            .getAttribute()
            .getType()
            .getJavaType()
            .isAnnotationPresent(Entity.class);
  }
}
