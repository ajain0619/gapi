package com.nexage.app.services.impl;

import static com.nexage.app.services.impl.support.SupportedCurrencyProvider.SUPPORTED_CUR_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParseException;
import com.nexage.app.dto.SupportedCurrencyDTO;
import com.nexage.app.services.impl.support.SupportedCurrencyProvider;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.assemblers.provisionable.ProvisionableUtils;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyProviderTest {

  @Mock private CustomObjectMapper mapper;

  @InjectMocks private SupportedCurrencyProvider currencyProvider;

  @Test
  void anExceptionShouldBeThrownWhenFileCannotBeLoaded() {
    SupportedCurrencyProvider provider = new SupportedCurrencyProvider(new CustomObjectMapper());
    SupportedCurrencyProvider providerSpy = spy(provider);
    String filePath = ProvisionableUtils.getStaticJsonFolder().concat(SUPPORTED_CUR_FILE_NAME);

    doReturn(null).when(providerSpy).getResourceAsStream(filePath);

    assertThrows(IllegalStateException.class, () -> providerSpy.loadCurrencies());
  }

  @Test
  void anExceptionShouldBeThrownWhenFileLoadedCannotBeParsed() throws IOException {
    when(mapper.readValue(nullable(InputStream.class), eq(SupportedCurrencyDTO[].class)))
        .thenThrow(JsonParseException.class);

    assertThrows(IllegalStateException.class, () -> currencyProvider.loadCurrencies());
  }
}
