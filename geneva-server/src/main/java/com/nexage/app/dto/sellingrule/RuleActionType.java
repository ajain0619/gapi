package com.nexage.app.dto.sellingrule;

import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum RuleActionType {
  FLOOR {
    @Override
    public void validateData(String inputData) {
      try {
        BigDecimal floor = new BigDecimal(inputData);
        log.debug("Parsed floor: {}", floor);
        if (floor.compareTo(BigDecimal.ZERO) <= 0) {
          log.error("Rule floor cannot be less than or equal to zero");
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FLOOR_PRICE);
        }
      } catch (Throwable t) {
        log.error("Unable to parse floor price because: {}", t.getMessage(), t);
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FLOOR_PRICE);
      }
    }

    @Override
    public String translateDataFromDtoToEntity(String data) {
      return roundFloor(data);
    }

    private String roundFloor(String data) {
      BigDecimal floor = new BigDecimal(data);
      return floor.setScale(2, RoundingMode.HALF_UP).toString();
    }
  },
  FILTER {
    @Override
    public void validateData(String inputData) {
      if (!FilterType.enumValueWithNameExists(inputData)) {
        log.error("There is no filter type with the given name: {}", inputData);
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_ACTION_DATA_INVALID_FILTER_TYPE);
      }
      log.debug("Filter type: {}", inputData);
    }

    @Override
    public String translateDataFromDtoToEntity(String data) {
      return FilterType.convertEnumNameToValue(data);
    }

    @Override
    public String translateDataFromEntityToDto(String data) {
      return FilterType.convertValueToEnumName(data);
    }
  },
  VISIBILITY {
    private static final String VISIBILITY_DELIMITER = ",";

    @Override
    public void validateData(String inputData) {
      try {
        String values = extractValues(inputData);
        log.debug("Visibility values: {}", values);
      } catch (Throwable t) {
        log.error("Unable to parse visibility controls because: {}", t.getLocalizedMessage(), t);
        throw new GenevaValidationException(ServerErrorCodes.SERVER_ACTION_DATA_INVALID_VISIBILITY);
      }
    }

    @Override
    public String translateDataFromDtoToEntity(String data) {
      return extractValues(data);
    }

    private String extractValues(String inputData) {
      Set<String> result = new HashSet<>();
      for (String visibilityType : inputData.split(VISIBILITY_DELIMITER, -1)) {
        result.add(VisibilitySignal.valueOf(visibilityType.trim()).toString());
      }
      return String.join(VISIBILITY_DELIMITER, result);
    }
  };

  public abstract void validateData(String inputData);

  public String translateDataFromDtoToEntity(String data) {
    return data;
  }

  public String translateDataFromEntityToDto(String data) {
    return data;
  }
}
