package com.nexage.app.search;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.model.search.MultiValueSearchParamsArgumentResolver;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SearchTestControllerIT extends BaseControllerItTest {

  @InjectMocks private SearchTestController searchTestController;

  @BeforeEach
  public void setUp() {
    openMocks(this);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(searchTestController)
            .setCustomArgumentResolvers(new MultiValueSearchParamsArgumentResolver())
            .build();
  }

  @Test
  void testSearchParseWithQfAndQo() throws Exception {
    String qf = "{key1=val1,key2 = val2,key3 =val3, key4= val4|val5}";
    String qo = "or";

    mockMvc
        .perform(get("/search-tests?qf={qf}&qo={qo}", qf, qo))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.operator", is("OR")))
        .andExpect(jsonPath("$.fields.key1[0]", is("val1")));
  }

  @Test
  void testSearchParseWithQf() throws Exception {
    String qf = "{key1=val1}";

    mockMvc
        .perform(get("/search-tests?qf={qf}", qf))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.operator", is("OR")))
        .andExpect(jsonPath("$.fields.key1[0]", is("val1")));
  }

  @Test
  void testSearchParseWithoutQf() throws Exception {
    mockMvc
        .perform(get("/search-tests"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.operator", is("OR")))
        .andExpect(jsonPath("$.fields", notNullValue()))
        .andExpect(jsonPath("$.fields", instanceOf(LinkedHashMap.class)))
        .andExpect(jsonPath("$.fields", anEmptyMap()));
  }
}
