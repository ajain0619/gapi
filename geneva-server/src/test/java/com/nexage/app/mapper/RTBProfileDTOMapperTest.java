package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RTBProfile.ScreeningLevel;
import com.nexage.admin.core.model.RTBProfileView;
import com.nexage.app.dto.RTBProfileDTO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RTBProfileDTOMapperTest {
  private static final RTBProfileDTOMapper MAPPER = Mappers.getMapper(RTBProfileDTOMapper.class);

  @Test
  void shouldMapRTBProfileViewToDTO() {

    RTBProfileView source = getRtbProfileView();
    RTBProfileDTO result = MAPPER.map(source);
    assertEquals(source.getPid(), result.getPid());
    assertEquals(source.getName(), result.getName());
  }

  @Test
  void shouldMapRTBProfileToDTO() {

    RTBProfile source = new RTBProfile();
    source.setName("Test Name");
    source.setVersion(1);
    RTBProfileDTO result = MAPPER.map(source);
    assertEquals(source.getPid(), result.getPid());
    assertEquals(source.getName(), result.getName());
  }

  @Test
  void shouldMapRTBProfileFromDTO() {

    RTBProfile source = new RTBProfile();
    source.setName("Test Name");
    source.setVersion(1);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setName("Test Name 2");
    rtbProfileDTO.setVersion(1);
    rtbProfileDTO.setDescription("Test Description");
    MAPPER.map(rtbProfileDTO, source);
    assertEquals(source.getName(), rtbProfileDTO.getName());
    assertEquals(source.getVersion(), rtbProfileDTO.getVersion());
    assertEquals(source.getDescription(), rtbProfileDTO.getDescription());
  }

  private RTBProfileView getRtbProfileView() {
    final long pid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();

    return new RTBProfileView() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Long getPid() {
        return pid;
      }

      @Override
      public int getAuctionType() {
        return 0;
      }

      @Override
      public String getBlockedAdTypes() {
        return null;
      }

      @Override
      public BigDecimal getPubNetLowReserve() {
        return null;
      }

      @Override
      public BigDecimal getPubNetReserve() {
        return null;
      }

      @Override
      public ScreeningLevel getScreeningLevel() {
        return null;
      }

      @Override
      public Integer getVersion() {
        return 1;
      }

      @Override
      public AlterReserve getAlterReserve() {
        return null;
      }

      @Override
      public BigDecimal getDefaultReserve() {
        return null;
      }

      @Override
      public boolean getIncludeConsumerId() {
        return false;
      }

      @Override
      public boolean getIncludeConsumerProfile() {
        return false;
      }

      @Override
      public boolean getIncludeDomainReferences() {
        return false;
      }

      @Override
      public boolean getIncludeGeoData() {
        return false;
      }

      @Override
      public BigDecimal getLowReserve() {
        return null;
      }

      @Override
      public Date getCreationDate() {
        return null;
      }

      @Override
      public String getDescription() {
        return null;
      }

      @Override
      public Date getLastUpdate() {
        return null;
      }
    };
  }
}
