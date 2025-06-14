package com.nexage.app.manager;

import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.InternalUserValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralExtendedUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUsersResponseDTO;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserStatus;
import com.ssp.geneva.sdk.onecentral.model.PasswordUpdateRequest;
import com.ssp.geneva.sdk.onecentral.model.UserMigrationRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service("oneCentralUserManager")
public class OneCentralUserManagerImpl implements OneCentralUserManager {

  private static final Set<String> USER_SCOPES = Collections.singleton("one");

  private final OneCentralSdkClient oneCentralSdkClient;
  private final OAuth2RestTemplate restTemplate;

  /** {@inheritDoc} */
  @Autowired
  public OneCentralUserManagerImpl(
      OneCentralSdkClient oneCentralSdkClient, OAuth2RestTemplate s2sTemplate) {
    this.oneCentralSdkClient = oneCentralSdkClient;
    this.restTemplate = s2sTemplate;
  }

  /** {@inheritDoc} */
  @Override
  public boolean createUserEnabled() {
    return oneCentralSdkClient.getOneCentralSdkConfigProperties().getSsoCreateOneCentralUser();
  }

  /** {@inheritDoc} */
  @Override
  public OneCentralUser createOneCentralUser(User user) {
    if (user.getRole() == Role.ROLE_API) {
      return createOneCentralApiUser(user);
    } else if (oneCentralUserNotExists(user.getEmail())) {
      return createNonExistingOneCentralUser(user);
    } else {
      return createOneCentralMigrationUser(user);
    }
  }

  /** {@inheritDoc} */
  @Override
  public OneCentralUser updateOneCentralUser(User user) {
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .username(user.getOneCentralUserName())
            .status(OneCentralUserStatus.ACTIVE)
            .internal(validateInternalUser(user))
            .build();
    var extendedRequestDto =
        OneCentralExtendedUserRequestDTO.builder()
            .userRequestDTO(request)
            .roleName(extractOneCentralRoleName(user))
            .sellerSeatEnabled(user.getSellerSeat() != null)
            .build();
    log.info("Seller seat enabled for user: {}", extendedRequestDto.isSellerSeatEnabled());
    var accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<OneCentralUser> response =
        oneCentralSdkClient.getUserService().updateUser(accessToken, extendedRequestDto);
    return launderOneCentralResponse(response);
  }

  /** {@inheritDoc} */
  @Override
  public String resetPassword(User user, String oldPassword, String newPassword) {
    PasswordUpdateRequest request =
        PasswordUpdateRequest.builder()
            .username(user.getOneCentralUserName())
            .oldPassword(oldPassword)
            .newPassword(newPassword)
            .build();
    var accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<String> response =
        oneCentralSdkClient.getUserManagementPasswordRepository().reset(accessToken, request);
    return launderOneCentralResponse(response);
  }

  private String extractOneCentralRoleName(User user) {
    String result = null;
    if (user.getRole() != null && user.getCompanyType() != null) {
      var sb = new StringBuilder();
      sb.append(user.getRole().toString()).append("_").append(user.getCompanyType().toString());
      result = sb.toString();
    }
    log.info("OneCentral role name for update: {}", result);
    return result;
  }

  private OneCentralUser createOneCentralApiUser(User user) {
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .notify(true)
            .activationRedirectionUrl(getRedirectUrl())
            .roleIds(parseRoleIds(getApiRoleIds()))
            .status(OneCentralUserStatus.PENDING_ACTIVATION)
            .internal(false)
            .systemUser(true)
            .apiUser(false)
            .scopes(USER_SCOPES)
            .contactName(user.getContactName())
            .contactEmail(user.getContactEmail())
            .build();
    var accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<OneCentralUser> response =
        oneCentralSdkClient.getUserManagementRepository().createUser(accessToken, request);
    return launderOneCentralResponse(response);
  }

  private OneCentralUser createNonExistingOneCentralUser(User user) {
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .notify(true)
            .activationRedirectionUrl(getRedirectUrl())
            .roleIds(parseRoleIds(getRoleIds()))
            .status(OneCentralUserStatus.PENDING_ACTIVATION)
            .internal(validateInternalUser(user))
            .scopes(USER_SCOPES)
            .build();
    var accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<OneCentralUser> response =
        oneCentralSdkClient.getUserManagementRepository().createUser(accessToken, request);
    return launderOneCentralResponse(response);
  }

  private boolean oneCentralUserNotExists(String email) {
    String accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<OneCentralUsersResponseDTO> response =
        oneCentralSdkClient.getUserManagementRepository().getUsersByEmail(accessToken, email);
    return response == null
        || response.getBody() == null
        || CollectionUtils.isEmpty(response.getBody().getUsers());
  }

  private OneCentralUser createOneCentralMigrationUser(User user) {
    UserMigrationRequest request =
        UserMigrationRequest.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .notify(true)
            .activationRedirectionUrl(getRedirectUrl())
            .systemName(oneCentralSdkClient.getOneCentralSdkConfigProperties().getSsoSystemName())
            .roleIds(parseRoleIds(getRoleIds()))
            .status(OneCentralUserStatus.PENDING_ACTIVATION)
            .internal(validateInternalUser(user))
            .build();
    var accessToken = restTemplate.getAccessToken().getValue();
    ResponseEntity<OneCentralUser> response =
        oneCentralSdkClient.getUserMigrationRepository().create(accessToken, request);
    return launderOneCentralResponse(response);
  }

  private long[] parseRoleIds(String ids) {
    try {
      return Arrays.stream(ids.split(",")).mapToLong(Long::parseLong).toArray();
    } catch (NumberFormatException e) {
      log.error("Role ids can't be converted to array of long: {} ", ids, e);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_ONECENTRAL_ROLE_IDS);
    }
  }

  private boolean validateInternalUser(User user) {
    return InternalUserValidator.isInternal(user);
  }

  private String getRedirectUrl() {
    return oneCentralSdkClient.getOneCentralSdkConfigProperties().getSsoUiBaseEndpoint();
  }

  private String getRoleIds() {
    return oneCentralSdkClient.getOneCentralSdkConfigProperties().getSsoRoleId();
  }

  private String getApiRoleIds() {
    return oneCentralSdkClient.getOneCentralSdkConfigProperties().getSsoApiUserRoleId();
  }

  private <U> U launderOneCentralResponse(ResponseEntity<U> response) {
    if (HttpStatus.OK != response.getStatusCode()) {
      log.error("One Central Rest call was not successful : {}", response.getStatusCode());
      throw new OneCentralException(OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR);
    }

    if (response.hasBody()) {
      return response.getBody();
    }

    log.error("One Central Rest call responds null : {}", response.getBody());
    throw new OneCentralException(OneCentralErrorCodes.ONECENTRAL_NULL_RESPONSE);
  }
}
