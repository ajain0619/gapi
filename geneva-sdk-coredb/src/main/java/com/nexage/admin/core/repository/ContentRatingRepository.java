package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ContentRating;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRatingRepository
    extends JpaRepository<ContentRating, Long>, JpaSpecificationExecutor<ContentRating> {

  List<ContentRating> findByRatingIn(Collection<String> ratings);
}
