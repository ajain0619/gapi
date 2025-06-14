package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.AppAlias;
import com.nexage.admin.core.repository.AppAliasRepository;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppAliasServiceImplTest {

  @InjectMocks private AppAliasServiceImpl appAliasService;

  @Mock private AppAliasRepository appAliasRepository;

  private String appAlias = "Samsung TV Plus";
  private Long pid;

  @BeforeEach
  public void setUp() {
    pid = RandomUtils.nextLong();
  }

  @Test
  void shouldFindAppAliasWhenAppAliasExists() {
    AppAlias expected = new AppAlias();
    expected.setAppAlias(appAlias);
    expected.setPid(pid);

    when(appAliasRepository.findByAppAlias(appAlias)).thenReturn(expected);

    AppAlias actual = appAliasService.findAppAlias(appAlias);

    assertEquals(expected.getAppAlias(), actual.getAppAlias());
    assertEquals(expected.getPid(), actual.getPid());
  }

  @Test
  void shouldCreateNewAppAliasWhenAppAliasDoesntExist() {
    AppAlias expected = new AppAlias();
    expected.setAppAlias("New Alias");
    expected.setPid(pid);

    when(appAliasRepository.findByAppAlias("New Alias")).thenReturn(null);
    when(appAliasService.createAppAlias("New Alias")).thenReturn(expected);

    AppAlias actual = appAliasService.findAppAlias("New Alias");

    assertEquals(expected.getAppAlias(), actual.getAppAlias());
    assertEquals(expected.getPid(), actual.getPid());
  }
}
