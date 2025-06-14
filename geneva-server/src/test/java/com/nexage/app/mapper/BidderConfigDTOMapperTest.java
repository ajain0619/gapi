package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.api.client.util.Lists;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.IdentityProviderView;
import com.nexage.admin.core.model.NativeVersion;
import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDenyAllowFilterListDTO;
import com.nexage.app.dto.BidderDeviceTypeDTO;
import com.nexage.app.dto.BidderRegionLimitDTO;
import com.nexage.app.dto.filter.FilterListDTO;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BidderConfigDTOMapperTest {

  @InjectMocks private BidderConfigDTOMapper mapper = new BidderConfigDTOMapperImpl();

  @Spy
  private BidderConfigDenyAllowFilterListDTOMapper bidderConfigDenyAllowFilterListDTOMapper =
      new BidderConfigDenyAllowFilterListDTOMapperImpl();

  @Test
  void dtoNativeVersionNull_mapsTo_modelNativeVersionsEmptySet() {
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setNativeVersion(null);
    BidderConfig model = mapper.map(dto);
    assertEquals(model.getNativeVersions(), Set.of());
  }

  @Test
  void dtoNativeVersionNonNull_mapsTo_modelNativeVersionsSetContainingThatValue() {
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setNativeVersion("1.1");
    BidderConfig model = mapper.map(dto);
    assertEquals(model.getNativeVersions(), Set.of(NativeVersion.v1_1));
  }

  @Test
  void modelNativeVersionsEmptySet_mapsTo_dtoNativeVersionNull() {
    BidderConfig model = new BidderConfig();
    model.setNativeVersions(Set.of());
    BidderConfigDTO dto = mapper.map(model);
    assertNull(dto.getNativeVersion());
  }

  @Test
  void modelNativeVersionsNonEmptySet_mapsTo_dtoNativeVersionHighestInSet() {
    BidderConfig model = new BidderConfig();

    model.setNativeVersions(Set.of(NativeVersion.v1_0));
    BidderConfigDTO dto = mapper.map(model);
    assertEquals("1.0", dto.getNativeVersion());

    model.setNativeVersions(Set.of(NativeVersion.v1_0, NativeVersion.v1_1));
    dto = mapper.map(model);
    assertEquals("1.1", dto.getNativeVersion());

    model.setNativeVersions(Set.of(NativeVersion.v1_0, NativeVersion.v1_1, NativeVersion.v1_2));
    dto = mapper.map(model);
    assertEquals("1.2", dto.getNativeVersion());
  }

  @Test
  void injectBackReferenceIntoAllowedDeviceTypes() {
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setAllowedDeviceTypes(Set.of(new BidderDeviceTypeDTO()));
    BidderConfig model = mapper.map(dto);
    BidderConfig backReference = model.getAllowedDeviceTypes().iterator().next().getBidderConfig();
    assertEquals(model, backReference);
  }

  @Test
  void injectBackReferenceIntoRegionLimits() {
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setRegionLimits(Set.of(new BidderRegionLimitDTO()));
    BidderConfig model = mapper.map(dto);
    BidderConfig backReference = model.getRegionLimits().iterator().next().getBidderConfig();
    assertEquals(model, backReference);
  }

  @Test
  void injectBackReferenceIntoBidderConfigDenyAllowFilterLists() {
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setBidderConfigDenyAllowFilterLists(Set.of(new BidderConfigDenyAllowFilterListDTO()));
    BidderConfig model = mapper.map(dto);
    BidderConfig backReference =
        model.getBidderConfigDenyAllowFilterLists().iterator().next().getBidderConfig();
    assertEquals(model, backReference);
  }

  @Test
  void shouldSetBothInclusiveAndNonInclusiveFilterListColumnsAsSame() {
    BidderConfigDenyAllowFilterListDTO bidderConfigDenyAllowFilterListDTO =
        new BidderConfigDenyAllowFilterListDTO();
    FilterListDTO filterListDTO = new FilterListDTO();
    filterListDTO.setPid(2);
    bidderConfigDenyAllowFilterListDTO.setFilterList(filterListDTO);
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setBidderConfigDenyAllowFilterLists(Set.of(bidderConfigDenyAllowFilterListDTO));
    BidderConfig model = mapper.map(dto);
    Set<BidderConfigDenyAllowFilterList> bidderConfigDenyAllowFilterListSet =
        model.getBidderConfigDenyAllowFilterLists();
    assertEquals(1, bidderConfigDenyAllowFilterListSet.size());
    assertEquals(
        2, Lists.newArrayList(bidderConfigDenyAllowFilterListSet).get(0).getFilterList().getPid());
    assertEquals(
        2,
        Lists.newArrayList(bidderConfigDenyAllowFilterListSet)
            .get(0)
            .getFilterListNonInclusive()
            .getPid());
  }

  @Test
  void shouldMapIdentityProviderPidSetToIdentityProviderViewSet() {
    // given
    BidderConfigDTO dto = new BidderConfigDTO();
    dto.setIdentityProviders(Set.of(1L, 2L));

    // when
    BidderConfig model = mapper.map(dto);

    // then
    assertEquals(2, model.getIdentityProviders().size());
    assertEquals(
        Set.of(new IdentityProviderView(1L), new IdentityProviderView(2L)),
        model.getIdentityProviders());
  }

  @Test
  void shouldMapIdentityProviderViewSetToIdentityProviderPidSet() {
    // given
    BidderConfig model = new BidderConfig();
    model.setIdentityProviders(Set.of(new IdentityProviderView(1L), new IdentityProviderView(2L)));

    // when
    BidderConfigDTO dto = mapper.map(model);

    // then
    assertEquals(2, model.getIdentityProviders().size());
    assertEquals(2, dto.getIdentityProviders().size());
    assertEquals(Set.of(1L, 2L), dto.getIdentityProviders());
  }

  @Test
  void shouldMapNullIdentityProviderPidSetToEmptyIdentityProviderViewSet() {
    // given
    BidderConfigDTO dto = new BidderConfigDTO();

    // when
    BidderConfig model = mapper.map(dto);

    // then
    assertNotNull(model.getIdentityProviders());
    assertEquals(0, model.getIdentityProviders().size());
  }

  @Test
  void shouldMapNullIdentityProviderViewSetToEmptyIdentityProviderPidSet() {
    // given
    BidderConfig model = new BidderConfig();

    // when
    BidderConfigDTO dto = mapper.map(model);

    // then
    assertNotNull(dto.getIdentityProviders());
    assertEquals(0, dto.getIdentityProviders().size());
  }
}
