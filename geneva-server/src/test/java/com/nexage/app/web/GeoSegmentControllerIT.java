package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.nexage.admin.core.model.GeoSegment;
import com.nexage.app.services.GeoSegmentService;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class GeoSegmentControllerIT extends BaseControllerItTest {

  @Mock private GeoSegmentService geoSegmentService;

  @InjectMocks private GeoSegmentController geoSegmentController;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(geoSegmentController).build();
  }

  @Test
  void testGetGeoSegments() throws Exception {
    ArrayList<GeoSegment> geoSegments = Lists.newArrayList(TestObjectsFactory.createGeoSegments(3));
    GeoSegment first = geoSegments.get(0);
    first.setWoeid(123456789L);
    when(geoSegmentService.getAllGeoSegments(
            anyString(), anyLong(), anyInt(), anyInt(), anyString(), anyString()))
        .thenReturn(geoSegments);
    mockMvc
        .perform(get("/geosegmentsinfo?query=a&filterType=0&limit=20&page=1&sort=name&dir=asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].pid", is(first.getPid())))
        .andExpect(jsonPath("$[0].segmentId", is(first.getSegmentId())))
        .andExpect(jsonPath("$[0].name", is(first.getName())))
        .andExpect(jsonPath("$[0].iso3Code", is(first.getIso3Code())))
        .andExpect(jsonPath("$[0].type", is(first.getType())))
        .andExpect(jsonPath("$[0].woeid", is(123456789)));
  }
}
