package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ContentGenre;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentGenreRepository
    extends JpaRepository<ContentGenre, Long>, JpaSpecificationExecutor<ContentGenre> {

  @Query(value = "SELECT COUNT(g) FROM ContentGenre g WHERE g.genre IN (:genres)")
  int existsByGenre(@Param("genres") Collection<String> genres);
}
