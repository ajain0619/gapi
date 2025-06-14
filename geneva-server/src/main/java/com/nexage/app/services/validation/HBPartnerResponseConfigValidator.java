package com.nexage.app.services.validation;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.HBPartnerResponseConfigConstraint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HBPartnerResponseConfigValidator
    extends BaseValidator<HBPartnerResponseConfigConstraint, String> {

  private static final String HBPARTNER_RESPONSE_CONFIG_SCHEMA =
      "/schema/hbpartner-response-config-schema.json";

  private JsonSchema schema;

  @PostConstruct
  public void init() throws Exception {
    try {
      schema = buildJsonSchema();
    } catch (Exception e) {
      log.error("Failed to load HB Partner's ResponseConfig JSON schema");
      throw e;
    }
  }

  /**
   * Validates the JSON string in {@code responseConfig} of {@link com.nexage.app.dto.HbPartnerDTO}
   * entity based on the JSON schema defined under the file
   * /schema/hbpartner-response-config-schema.json
   */
  @Override
  public boolean isValid(String responseConfig, ConstraintValidatorContext context) {
    if (isNull(responseConfig)) {
      return true;
    }
    if (!validJson(responseConfig)) {
      return addConstraintMessage(context, getAnnotation().message());
    }
    return true;
  }

  private JsonSchema buildJsonSchema() throws IOException, ProcessingException {
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonNode schemaNode = JsonLoader.fromString(readSchemaFromFile());
    return factory.getJsonSchema(schemaNode);
  }

  private String readSchemaFromFile() throws IOException {
    try (InputStream resInputStream =
        ResourceLoader.class.getResourceAsStream(HBPARTNER_RESPONSE_CONFIG_SCHEMA)) {
      return new BufferedReader(new InputStreamReader(resInputStream))
          .lines()
          .collect(Collectors.joining());
    }
  }

  private boolean validJson(String jsonData) {
    JsonNode nodeData;
    try {
      nodeData = JsonLoader.fromString(jsonData);
    } catch (IOException e) {
      log.error("ResponseConfig JSON is incorrectly formatted. Please check if the JSON is valid");
      return false;
    }

    try {
      if (!schema.validate(nodeData).isSuccess()) {
        log.error("ResponseConfig JSON does not match the expected JSON schema");
        return false;
      }
    } catch (ProcessingException e) {
      log.error("Could not process ResponseConfig JSON schema validity.");
      return false;
    }
    if (nodeData.isEmpty()) {
      log.warn("Empty JSON used in ResponseConfig JSON");
      return false;
    }
    return true;
  }

  private boolean addConstraintMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    super.buildConstraintViolationWithTemplate(context, message);
    return false;
  }
}
