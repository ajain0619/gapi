package com.nexage.app.services.user;

import com.nexage.app.dto.user.UserDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDTOService {

  /**
   * Get user by userPid
   *
   * @param userPid the pid of the user
   * @return {@link UserDTO} object
   */
  UserDTO getUser(Long userPid);

  /**
   * Gets all {@link UserDTO} under request criteria, returning a paginated response.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link UserDTO} object
   */
  Page<UserDTO> getAllUsers(Set<String> qf, String qt, Pageable pageable);

  /**
   * Create new user
   *
   * @param userDto of user to be created
   * @return userDto of created user
   */
  UserDTO createUser(UserDTO userDto);

  /**
   * Update the user details
   *
   * @param userDto of user to be updated
   * @param userPid user pid
   * @return userDto of updated user.
   */
  UserDTO updateUser(UserDTO userDto, Long userPid);
}
