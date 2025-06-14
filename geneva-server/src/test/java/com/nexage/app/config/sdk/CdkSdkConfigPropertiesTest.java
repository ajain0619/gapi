package com.nexage.app.config.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CdkSdkConfigPropertiesTest {

  Boolean ckmsEnabled = true;
  String crsSsoClientId = "crsSsoClientId";
  Boolean crsSsoSecretEncrypted = true;
  String crsSsoSecret = "crsSsoSecret";
  String genevaMetricsJmxDomain = "genevaMetricsJmxDomain";
  String crsSsoCkmsKeyClient = "crsSsoCkmsKeyClient";
  String crsSsoCkmsKeySecret = "crsSsoCkmsKeySecret";
  String crsSsoCkmsKeyGroup = "crsSsoCkmsKeyGroup";

  CdkSdkConfigProperties props;

  @BeforeEach
  public void before() {
    props =
        new CdkSdkConfigProperties(
            ckmsEnabled,
            crsSsoClientId,
            crsSsoSecretEncrypted,
            crsSsoSecret,
            genevaMetricsJmxDomain,
            crsSsoCkmsKeyClient,
            crsSsoCkmsKeySecret,
            crsSsoCkmsKeyGroup);
  }

  @Test
  void testGetters() {
    assertNotNull(props);
    assertEquals(ckmsEnabled, props.getCkmsEnabled());
    assertEquals(crsSsoClientId, props.getCrsSsoClientId());
    assertEquals(crsSsoSecretEncrypted, props.getCrsSsoSecretEncrypted());
    assertEquals(crsSsoSecret, props.getCrsSsoSecret());
    assertEquals(genevaMetricsJmxDomain, props.getGenevaMetricsJmxDomain());
    assertEquals(crsSsoCkmsKeyClient, props.getCrsSsoCkmsKeyClient());
    assertEquals(crsSsoCkmsKeySecret, props.getCrsSsoCkmsKeySecret());
    assertEquals(crsSsoCkmsKeyGroup, props.getCrsSsoCkmsKeyGroup());
  }
}
