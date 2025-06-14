package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.util.assemblers.context.PublisherAdSourceDefaultsContext;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PublisherAdSourceDefaultsAssembler
    extends Assembler<
        PublisherAdSourceDefaultsDTO, SellerAdSource, PublisherAdSourceDefaultsContext> {

  public static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "version",
          "sellerPid",
          "adSourcePid",
          "username",
          "password",
          "apiToken",
          "apiKey");

  public PublisherAdSourceDefaultsDTO make(
      PublisherAdSourceDefaultsContext context, SellerAdSource dbDefaults) {
    return make(context, dbDefaults, DEFAULT_FIELDS);
  }

  public PublisherAdSourceDefaultsDTO make(
      PublisherAdSourceDefaultsContext context, AdSource adsource) {
    return make(context, adsource, DEFAULT_FIELDS);
  }

  public PublisherAdSourceDefaultsDTO make(
      PublisherAdSourceDefaultsContext context, AdSource adsource, Set<String> fields) {
    PublisherAdSourceDefaultsDTO.Builder adSourceDefaults =
        PublisherAdSourceDefaultsDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;
    return adSourceDefaults
        .withVersion(adsource.getVersion(), fieldsToMap)
        .withAdSourcePid(adsource.getPid(), fieldsToMap)
        .build();
  }

  public PublisherAdSourceDefaultsDTO make(
      PublisherAdSourceDefaultsContext context, SellerAdSource dbdefaults, Set<String> fields) {
    PublisherAdSourceDefaultsDTO.Builder adSourceDefaults =
        PublisherAdSourceDefaultsDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;
    return adSourceDefaults
        .withPid(dbdefaults.getPid(), fieldsToMap)
        .withVersion(dbdefaults.getVersion(), fieldsToMap)
        .withSellerPid(dbdefaults.getSellerPid(), fieldsToMap)
        .withAdSourcePid(dbdefaults.getAdSourcePid(), fieldsToMap)
        .withUserName(dbdefaults.getUsername(), fieldsToMap)
        .withPassword(dbdefaults.getPassword(), fieldsToMap)
        .withApiToken(dbdefaults.getApiToken(), fieldsToMap)
        .withApiKey(dbdefaults.getApiKey(), fieldsToMap)
        .build();
  }

  public SellerAdSource apply(
      PublisherAdSourceDefaultsContext context,
      SellerAdSource persistentObject,
      PublisherAdSourceDefaultsDTO adSourceDefaults) {
    persistentObject.setSellerPid(adSourceDefaults.getSellerPid());
    persistentObject.setAdSourcePid(adSourceDefaults.getAdSourcePid());
    persistentObject.setUsername(adSourceDefaults.getUsername());
    persistentObject.setPassword(adSourceDefaults.getPassword());
    persistentObject.setApiKey(adSourceDefaults.getApiKey());
    persistentObject.setApiToken(adSourceDefaults.getApiToken());

    return persistentObject;
  }
}
