package com.nexage.app.web;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.services.user.UserDTOService;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "/v1/users")
@RequestMapping(value = "/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserDTOController {

  private final UserDTOService userDTOService;

  public UserDTOController(UserDTOService userDTOService) {
    this.userDTOService = userDTOService;
  }

  /**
   * Returns paged list of users belonging to a seller seat pid or company pid or or only the
   * current user or everything if none are are not set .
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return paged list of UserDTO
   */
  @Operation(
      summary =
          "Get all users for either seller seat pid or company pid or or everything if both are not set")
  @ApiResponse(content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Page<UserDTO>> getAllUsers(
      @PageableDefault(sort = "name") Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {

    return ResponseEntity.ok(userDTOService.getAllUsers(qf, qt, pageable));
  }

  /**
   * Returns the user associated with the userPid sent
   *
   * @param userPid user Pid
   * @return the given user
   */
  @Operation(summary = "Get user by userPid")
  @ApiResponse(content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{userPid}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<UserDTO> getUser(@PathVariable(value = "userPid") Long userPid) {
    return ResponseEntity.ok(userDTOService.getUser(userPid));
  }

  /**
   * Create a new user
   *
   * @param userDto of user to be created
   * @return wrapped userDto of created user
   */
  @Operation(summary = "Create user")
  @ApiResponse(content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<UserDTO> createUser(
      @RequestBody @Validated(value = {Default.class, CreateGroup.class}) UserDTO userDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userDTOService.createUser(userDto));
  }

  /**
   * Updates the user details
   *
   * @param userDto of user to be updated
   * @return wrapped userDto of updated user.
   */
  @Operation(summary = "Update user")
  @ApiResponse(content = @Content(schema = @Schema(implementation = UserDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping(path = "{userPid}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable Long userPid,
      @RequestBody @Validated(value = {Default.class, UpdateGroup.class}) UserDTO userDto) {
    return ResponseEntity.ok(userDTOService.updateUser(userDto, userPid));
  }
}
