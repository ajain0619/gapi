package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.tag.TagDTO;
import com.nexage.app.services.TagDTOService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(MockitoExtension.class)
class TagDTOControllerTest {

  @Mock private TagDTOService tagDTOService;
  @Mock private MockMvc mockMvc;

  @InjectMocks private TagDTOController tagDTOController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(tagDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void findTags() throws Throwable {
    List<TagDTO> tagDTOList = TestObjectsFactory.createTagDTOs(10);
    Page tagSummaryDTOPage = new PageImpl(tagDTOList);
    TagDTO first = tagDTOList.get(0);
    when(tagDTOService.getTags(any(), any(), any(), any())).thenReturn(tagSummaryDTOPage);
    mockMvc
        .perform(get("/v1/sellers/123/sites/123/placements/123/tags?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }
}
