package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.app.util.assemblers.context.PublisherAdSourceDefaultsContext;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublisherAdSourceDefaultsAssemblerTest {

  private static final Set<String> fields =
      Set.of(
          "pid",
          "version",
          "sellerPid",
          "adSourcePid",
          "username",
          "password",
          "apiToken",
          "apiKey");

  @Test
  void testMake() {
    SellerAdSource dbDefaults = new SellerAdSource();
    dbDefaults.setPid(100L);
    dbDefaults.setVersion(3);
    dbDefaults.setSellerPid(200L);
    dbDefaults.setAdSourcePid(300L);
    dbDefaults.setUsername("user");
    dbDefaults.setPassword("password");
    dbDefaults.setApiToken("token");
    dbDefaults.setApiKey("apikey");

    var assembler = new PublisherAdSourceDefaultsAssembler();
    var context = PublisherAdSourceDefaultsContext.newBuilder().build();

    var out = assembler.make(context, dbDefaults, fields);
    assertEquals(dbDefaults.getPid(), out.getPid());
    assertEquals(dbDefaults.getVersion(), out.getVersion().intValue());
    assertEquals(dbDefaults.getSellerPid(), out.getSellerPid());
    assertEquals(dbDefaults.getAdSourcePid(), out.getAdSourcePid());
    assertEquals(dbDefaults.getUsername(), out.getUsername());
    assertEquals(dbDefaults.getPassword(), out.getPassword());
    assertEquals(dbDefaults.getApiToken(), out.getApiToken());
    assertEquals(dbDefaults.getApiKey(), out.getApiKey());
  }
}
