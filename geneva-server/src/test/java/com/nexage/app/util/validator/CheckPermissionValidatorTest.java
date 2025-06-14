package com.nexage.app.util.validator;

import static com.nexage.admin.core.model.User.Role.ROLE_ADMIN;
import static com.nexage.admin.core.model.User.Role.ROLE_API;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.User.Role;
import com.nexage.app.security.UserContext;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CheckPermissionValidatorTest {

  @Mock private ConstraintValidatorContext context;
  @Mock private CheckPermissionConstraint checkPermissionConstraint;

  @Mock UserContext userContext;
  @InjectMocks private CheckPermissionValidator validator = new CheckPermissionValidator();

  @BeforeEach
  public void setup() {
    initializeContext();
    initializeConstraint();
  }

  @Test
  void shouldReturnTrueWhenUserIsNexageUser() {
    when(checkPermissionConstraint.checkIfNexageUser()).thenReturn(true);
    when(userContext.isNexageUser()).thenReturn(true);
    assertTrue(validator.isValid("newValue", context));
  }

  @Test
  void shouldReturnTrueWhenUserIsNexageUserAndHasRoleSpecifiedAmongOtherRoles() {
    when(checkPermissionConstraint.checkIfNexageUser()).thenReturn(true);
    when(checkPermissionConstraint.roles()).thenReturn(new Role[] {ROLE_ADMIN, ROLE_API});

    lenient().when(userContext.isNexageUser()).thenReturn(true);
    assertTrue(validator.isValid("newValue", context));
  }

  @Test
  void shouldReturnTrueWhenUserDoesNotHaveRoleAndNotNexageAndObjectIsNull() {
    when(checkPermissionConstraint.checkIfNexageUser()).thenReturn(true);
    when(checkPermissionConstraint.roles()).thenReturn(new Role[] {ROLE_API});

    when(userContext.isNexageUser()).thenReturn(false);
    assertTrue(validator.isValid(null, context));
  }

  @Test
  void shouldReturnFalseWhenUserDoesNotHaveRoleAndNotNexageAndObjectIsNotEmpty() {
    assertFalse(validator.isValid("valueShouldNotBeSet", context));
    verify(context).buildConstraintViolationWithTemplate(checkPermissionConstraint.message());
  }

  @Test
  void shouldReturnTrueWhenUserIsNexageUserAndDoesNotHaveRoleSpecified() {
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionConstraint.checkIfNexageUser()).thenReturn(true);
    when(checkPermissionConstraint.roles()).thenReturn(new Role[] {ROLE_API});

    assertTrue(validator.isValid("newValue", context));
  }

  private void initializeContext() {
    ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }

  private void initializeConstraint() {
    lenient().when(checkPermissionConstraint.message()).thenReturn("Invalid for site.type");
    when(checkPermissionConstraint.roles()).thenReturn(new Role[] {});
    when(checkPermissionConstraint.checkIfNexageUser()).thenReturn(false);
    ReflectionTestUtils.setField(validator, "annotation", checkPermissionConstraint);
  }
}
