package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RTBProfileLibraryRepository extends JpaRepository<RtbProfileLibrary, Long> {

  Optional<RtbProfileLibrary> findByPidAndPublisherPid(Long pid, Long publisherPid);

  List<RtbProfileLibrary> findAllByPublisherPid(Long publisherPid);

  List<RtbProfileLibrary> findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel privilegeLevel);

  List<RtbProfileLibrary> findAllByPublisherPidAndPrivilegeLevel(
      Long publisherPid, RTBProfileLibraryPrivilegeLevel privilegeLevel);
}
