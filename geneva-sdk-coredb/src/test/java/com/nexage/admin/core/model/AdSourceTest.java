package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.Status;
import org.junit.jupiter.api.Test;

class AdSourceTest {

  public static final String EMPTY_STRING = "";
  private static final String SPACE = " ";
  private static final String STRING = "String";
  private static final long PID = 1234L;
  private static final String PID_STRING = "PI123";
  private static final String PARAM_MAP =
      "pid=PI123 ,pname=Primary Name, sid=none, sname=Secondary Name";
  private static final String PRIMARY_NAME = "Primary Name";
  private static final String PARAM_DEFAULT = "pid=PIDef ,pname= ,sid= , sname=2Def";
  private static final String SECONDARY_NAME = "Secondary Name";
  private static final String NONE = "none";
  private static final String SECONDARY_ID = "secondaryId";
  private static final String PID_DEFAULT = "pidDefault";

  @Test
  void shouldTestTransients() {

    AdSource adSource = new AdSource();
    adSource.setPid(PID);
    adSource.setStatus(Status.ACTIVE);
    assertEquals(Status.ACTIVE.asInt(), adSource.getStatusVal().intValue());
    adSource.setStatus(Status.DELETED);
    assertEquals(Status.DELETED.asInt(), adSource.getStatusVal().intValue());
    adSource.setStatus(Status.INACTIVE);
    assertEquals(Status.INACTIVE.asInt(), adSource.getStatusVal().intValue());

    adSource.setStatusVal(Status.ACTIVE.asInt());
    assertEquals(Status.ACTIVE, adSource.getStatus());
    adSource.setStatusVal(Status.DELETED.asInt());
    assertEquals(Status.DELETED, adSource.getStatus());
    adSource.setStatusVal(Status.INACTIVE.asInt());
    assertEquals(Status.INACTIVE, adSource.getStatus());
  }

  @Test
  void shouldTestParamMaps() {
    AdSource adSource = new AdSource();

    // check handling of null fields
    adSource.setParamMap(null);
    adSource.setParamDefault(null);
    adSource.setParamRequired(null);

    assertEquals(NONE, adSource.getPrimaryId());
    assertEquals(NONE, adSource.getPrimaryName());
    assertEquals(NONE, adSource.getSecondaryId());
    assertEquals(NONE, adSource.getSecondaryName());
    assertEquals(EMPTY_STRING, adSource.getPrimaryIdDefault());
    assertEquals(EMPTY_STRING, adSource.getPrimaryNameDefault());
    assertEquals(EMPTY_STRING, adSource.getSecondaryIdDefault());
    assertEquals(EMPTY_STRING, adSource.getSecondaryNameDefault());
    assertFalse(adSource.isPrimaryIdRequired());
    assertFalse(adSource.isPrimaryNameRequired());
    assertFalse(adSource.isSecondaryIdRequired());
    assertFalse(adSource.isSecondaryNameRequired());

    adSource.setPrimaryId(null);
    adSource.setParamMap(PARAM_MAP);
    adSource.setParamDefault(PARAM_DEFAULT);
    adSource.setParamRequired("pid,sname");

    assertEquals(PID_STRING, adSource.getPrimaryId());
    assertEquals(PRIMARY_NAME, adSource.getPrimaryName());
    assertEquals(NONE, adSource.getSecondaryId());
    assertEquals(SECONDARY_NAME, adSource.getSecondaryName());
    assertTrue(adSource.isPrimaryIdRequired());
    assertFalse(adSource.isPrimaryNameRequired());
    assertFalse(adSource.isSecondaryIdRequired());
    assertTrue(adSource.isSecondaryNameRequired());

    adSource.setPrimaryId(" ");
    adSource.setSecondaryId(SECONDARY_ID);
    adSource.setSecondaryIdRequired(true);
    adSource.setPrimaryIdDefault(PID_DEFAULT);
    adSource.setSecondaryNameDefault(null);

    // the mapsplitter cleans up spacing between args and equal signs
    assertEquals(
        "pid=none,pname=Primary Name,sid=secondaryId,sname=Secondary Name", adSource.getParamMap());
    assertEquals("pid=pidDefault,pname=,sid=,sname=", adSource.getParamDefault());
    assertEquals("pid,sid,sname", adSource.getParamRequired());
  }

  @Test
  void shouldReturnUrlTemplate() {
    // given
    AdSource adSource = new AdSource();
    adSource.setUrlTemplate(SPACE + STRING + SPACE);

    // when
    String urlTemplate = adSource.getUrlTemplate();

    // then
    assertEquals(STRING, urlTemplate);
  }

  @Test
  void shouldReturnResponseParsingConfig() {
    // given
    AdSource adSource = new AdSource();
    adSource.setResponseParsingConfig(SPACE + STRING + SPACE);

    // when
    String responseParsingConfig = adSource.getResponseParsingConfig();

    // then
    assertEquals(STRING, responseParsingConfig);
  }

  @Test
  void shouldReturnPostTemplate() {
    // given
    AdSource adSource = new AdSource();
    adSource.setPostTemplate(SPACE + STRING + SPACE);

    // when
    String postTemplate = adSource.getPostTemplate();

    // then
    assertEquals(STRING, postTemplate);
  }

  @Test
  void shouldReturnGetTemplate() {
    // given
    AdSource adSource = new AdSource();
    adSource.setGetTemplate(SPACE + STRING + SPACE);

    // when
    String getTemplate = adSource.getGetTemplate();

    // then
    assertEquals(STRING, getTemplate);
  }

  @Test
  void shouldReturnExtraPostTemplate() {
    // given
    AdSource adSource = new AdSource();
    adSource.setExtraPostTemplate(SPACE + STRING + SPACE);

    // when
    String extraPostTemplate = adSource.getExtraPostTemplate();

    // then
    assertEquals(STRING, extraPostTemplate);
  }

  @Test
  void shouldReturnExtraGetTemplate() {
    // given
    AdSource adSource = new AdSource();
    adSource.setExtraGetTemplate(SPACE + STRING + SPACE);

    // when
    String extraGetTemplate = adSource.getExtraGetTemplate();

    // then
    assertEquals(STRING, extraGetTemplate);
  }

  @Test
  void shouldReturnPrimaryIdRequired() {
    // given
    AdSource adSource = new AdSource();
    adSource.setParamMap(PARAM_MAP);
    adSource.setPrimaryIdRequired(Boolean.TRUE);

    // when
    String returnedPid = adSource.getPrimaryId();

    // then
    assertEquals(PID_STRING, returnedPid);
  }

  @Test
  void shouldReturnPrimaryName() {
    // given
    AdSource adSource = new AdSource();
    adSource.setPrimaryName(PRIMARY_NAME);

    // when
    String returnedName = adSource.getPrimaryName();

    // then
    assertEquals(PRIMARY_NAME, returnedName);
  }

  @Test
  void shouldReturnPrimaryNameDefault() {
    // given
    AdSource adSource = new AdSource();
    adSource.setPrimaryNameDefault(PRIMARY_NAME);

    // when
    String returnedPrimaryNameDefault = adSource.getPrimaryNameDefault();

    // then
    assertEquals(PRIMARY_NAME, returnedPrimaryNameDefault);
  }

  @Test
  void shouldReturnPrimaryNameRequired() {
    // given
    AdSource adSource = new AdSource();
    adSource.setParamMap(PARAM_MAP);
    adSource.setPrimaryNameRequired(Boolean.TRUE);

    // when
    String returnedName = adSource.getPrimaryName();

    // then
    assertEquals(PRIMARY_NAME, returnedName);
  }

  @Test
  void shouldReturnSecondaryIdDefault() {
    // given
    AdSource adSource = new AdSource();
    adSource.setSecondaryIdDefault(SECONDARY_ID);

    // when
    String returnedSid = adSource.getSecondaryIdDefault();

    // then
    assertEquals(SECONDARY_ID, returnedSid);
  }

  @Test
  void shouldReturnSecondaryNameRequired() {
    // given
    AdSource adSource = new AdSource();
    adSource.setParamMap(PARAM_MAP);
    adSource.setSecondaryNameRequired(Boolean.TRUE);

    // when
    String returnedSecondaryNameRequired = adSource.getSecondaryName();

    // then
    assertEquals(SECONDARY_NAME, returnedSecondaryNameRequired);
  }

  @Test
  void shouldReturnSecondaryName() {
    // given
    AdSource adSource = new AdSource();
    adSource.setSecondaryName(SECONDARY_NAME);

    // when
    String returnedSecondaryName = adSource.getSecondaryName();

    // then
    assertEquals(SECONDARY_NAME, returnedSecondaryName);
  }

  @Test
  void shouldReturnEthnicity() {
    // given / when
    String africanAmerican =
        AdSource.Ethnicity.defaultStringFromMap.get(AdSource.Ethnicity.African_American);
    String asian = AdSource.Ethnicity.defaultStringFromMap.get(AdSource.Ethnicity.Asian);
    String hispanic = AdSource.Ethnicity.defaultStringFromMap.get(AdSource.Ethnicity.Hispanic);
    String white = AdSource.Ethnicity.defaultStringFromMap.get(AdSource.Ethnicity.White);
    String other = AdSource.Ethnicity.defaultStringFromMap.get(AdSource.Ethnicity.Other);

    // then
    assertNotNull(africanAmerican);
    assertNotNull(asian);
    assertNotNull(hispanic);
    assertNotNull(white);
    assertNotNull(other);

    assertEquals("0", africanAmerican);
    assertEquals("1", asian);
    assertEquals("2", hispanic);
    assertEquals("3", white);
    assertEquals("4", other);
  }

  @Test
  void shouldReturnGender() {
    // given / when
    String male = AdSource.Gender.defaultStringFromMap.get(AdSource.Gender.Male);
    String female = AdSource.Gender.defaultStringFromMap.get(AdSource.Gender.Female);
    String other = AdSource.Gender.defaultStringFromMap.get(AdSource.Gender.Other);

    // then
    assertNotNull(male);
    assertNotNull(female);
    assertNotNull(other);

    assertEquals("M", male);
    assertEquals("F", female);
    assertEquals("O", other);
  }

  @Test
  void shouldReturnMaritalStatus() {
    // given / when
    String single = AdSource.MaritalStatus.defaultStringFromMap.get(AdSource.MaritalStatus.Single);
    String married =
        AdSource.MaritalStatus.defaultStringFromMap.get(AdSource.MaritalStatus.Married);
    String divorced =
        AdSource.MaritalStatus.defaultStringFromMap.get(AdSource.MaritalStatus.Divorced);
    String other = AdSource.MaritalStatus.defaultStringFromMap.get(AdSource.MaritalStatus.Other);

    // then
    assertNotNull(single);
    assertNotNull(married);
    assertNotNull(divorced);
    assertNotNull(other);

    assertEquals("S", single);
    assertEquals("M", married);
    assertEquals("D", divorced);
    assertEquals("O", other);
  }
}
