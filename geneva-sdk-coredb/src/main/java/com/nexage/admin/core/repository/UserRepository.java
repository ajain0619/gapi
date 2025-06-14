package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  boolean existsByUserNameAndPid(String userName, Long pid);

  Optional<User> findByUserName(String userName);

  Optional<User> findByOneCentralUserName(String oneCentralUserName);

  Optional<User> findByEmail(String email);

  List<User> findAllBySellerSeat_Pid(Long sellerSeatPid);

  boolean existsByUserNameAndPidNot(String userName, Long pid);

  boolean existsByEmailAndPidNot(String email, Long pid);

  @Query(
      value =
          "DELETE FROM company_app_user WHERE company_id=:companyPid AND user_id IN (:usersPids)",
      nativeQuery = true)
  @Modifying
  void deleteCompanyAppUserByCompanyPidAndUserPid(
      @Param("companyPid") Long companyPid, @Param("usersPids") Set<Long> usersPids);
}
