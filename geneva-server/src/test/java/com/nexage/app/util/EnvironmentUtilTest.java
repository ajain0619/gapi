package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class EnvironmentUtilTest {

  @InjectMocks private EnvironmentUtil environmentUtil;

  @Mock private Environment environment;

  private static final String[] NULL_PROFILE = null;
  private static final String[] EMPTY_PROFILE = {""};
  private static final String[] SINGLE_ITEM_NO_DATA = {"profile"};
  private static final String[] SINGLE_ITEM_CONTAINING_AWS_IN_SUBSTRING = {"non_aws_profile"};
  private static final String[] SINGLE_ITEM_WITH_AWS_LOWER_CASE = {"aws"};
  private static final String[] SINGLE_ITEM_WITH_AWS_UPPER_CASE = {"AWS"};
  private static final String[] AWS_AS_FIRST_PROFILE_ITEM = {"aws", "this_profile", "that_profile"};
  private static final String[] AWS_AS_INTERMEDIATE_PROFILE_ITEM = {
    "this_profile", "aws", "that_profile"
  };
  private static final String[] AWS_AS_LAST_PROFILE_ITEM = {"this_profile", "that_profile", "aws"};

  @Test
  void isAwsProfileActiveReturnsFalseForNullActiveProfile() {
    when(environment.getActiveProfiles()).thenReturn(NULL_PROFILE);
    assertFalse(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsFalseForEmptyActiveProfile() {
    when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILE);
    assertFalse(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsFalseForSingleItemActiveProfileWithoutAws() {
    when(environment.getActiveProfiles()).thenReturn(SINGLE_ITEM_NO_DATA);
    assertFalse(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsFalseForSingleItemActiveProfileContainingAwsInSubstring() {
    when(environment.getActiveProfiles()).thenReturn(SINGLE_ITEM_CONTAINING_AWS_IN_SUBSTRING);
    assertFalse(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsTrueForSingleItemActiveProfileWithAwsLowerCase() {
    when(environment.getActiveProfiles()).thenReturn(SINGLE_ITEM_WITH_AWS_LOWER_CASE);
    assertTrue(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsTrueForSingleItemActiveProfileWithAwsUpperCase() {
    when(environment.getActiveProfiles()).thenReturn(SINGLE_ITEM_WITH_AWS_UPPER_CASE);
    assertTrue(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsTrueForActiveProfileWithAwsAsFirstProfile() {
    when(environment.getActiveProfiles()).thenReturn(AWS_AS_FIRST_PROFILE_ITEM);
    assertTrue(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsTrueForActiveProfileWithAwsAsIntermediateProfile() {
    when(environment.getActiveProfiles()).thenReturn(AWS_AS_INTERMEDIATE_PROFILE_ITEM);
    assertTrue(environmentUtil.isAwsEnvironment());
  }

  @Test
  void isAwsProfileActiveReturnsTrueForActiveProfileWithAwsAsLastProfile() {
    when(environment.getActiveProfiles()).thenReturn(AWS_AS_LAST_PROFILE_ITEM);
    assertTrue(environmentUtil.isAwsEnvironment());
  }
}
