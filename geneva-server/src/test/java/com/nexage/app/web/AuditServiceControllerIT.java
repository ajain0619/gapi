package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.app.dto.AuditDeltaResponseDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.AuditService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.publisher.PublisherSelfServeControllerIT;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class AuditServiceControllerIT {
  private static final String GET_AUDIT_REVISIONS_URL =
      "/audit/publisher/{publisher}/position/{position}/revision/{revisionNumber}";

  private MockMvc mockMvc;

  @Autowired
  @Qualifier("jsonConverter")
  private MappingJackson2HttpMessageConverter converter;

  @Mock private AuditService auditService;
  @Mock private UserContext userContext;
  @Mock SpringUserDetails springUserDetails;

  @InjectMocks private AuditServiceController auditServiceController;

  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  public void setUp() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(auditServiceController)
            .setMessageConverters(converter)
            .build();
  }

  @Test
  @SneakyThrows
  void getPositionRevisions() {
    String expected = getData(ResourcePath.UPDATED_PSS_POSITION_ER.getFilePath());
    String revisionResponseAfter = getData(ResourcePath.REVISION_RESPONSE_AFTER.getFilePath());
    String revisionResponseBefore = getData(ResourcePath.REVISION_RESPONSE_BEFORE.getFilePath());

    Object responseAfter = mapper.readValue(revisionResponseAfter, Object.class);
    Object responseBefore = mapper.readValue(revisionResponseBefore, Object.class);

    AuditResponseAtRevision responseAtRevision = new AuditResponseAtRevision();

    Map<String, Object> originalData = Collections.singletonMap("Position", responseBefore);
    Map<String, Object> changedData = Collections.singletonMap("Position", responseAfter);

    responseAtRevision.setBefore(originalData);
    responseAtRevision.setAfter(changedData);

    when(auditService.getEntityForRevision(anyLong(), anyLong(), anyLong(), any()))
        .thenReturn(responseAtRevision);

    MvcResult result =
        mockMvc
            .perform(get(GET_AUDIT_REVISIONS_URL, 100, 100, 617))
            .andExpect(status().isOk())
            .andReturn();

    AuditDeltaResponseDTO checking =
        mapper.readValue(result.getResponse().getContentAsString(), AuditDeltaResponseDTO.class);
    AuditDeltaResponseDTO exp = mapper.readValue(expected, AuditDeltaResponseDTO.class);

    assertEquals(exp.getDelta(), checking.getDelta());
  }

  private String getData(String name) throws IOException {
    return ResourceLoader.getResource(PublisherSelfServeControllerIT.class, name);
  }

  @Getter
  enum ResourcePath {
    REVISION_RESPONSE_AFTER(
        "/data/controllers/publisher_self_serve/positions/create/AuditAfterJson.json"),
    REVISION_RESPONSE_BEFORE(
        "/data/controllers/publisher_self_serve/positions/create/AuditBeforeJson.json"),
    UPDATED_PSS_POSITION_ER(
        "/data/controllers/publisher_self_serve/positions/expected_results/RevisionPosition_ER.json");

    private final String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
