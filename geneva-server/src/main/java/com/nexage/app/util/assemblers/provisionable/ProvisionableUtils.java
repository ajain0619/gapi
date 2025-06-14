package com.nexage.app.util.assemblers.provisionable;

import com.google.common.io.BaseEncoding;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProvisionableUtils {

  public static String encodeId(String id) {
    return id != null ? BaseEncoding.base64().encode(id.getBytes()) : null;
  }

  public static String encodeId(Long id) {
    return id != null ? encodeId(id.toString()) : null;
  }

  public static Long decodeId(String id) {
    return id != null ? Long.valueOf(new String(BaseEncoding.base64().decode(id))) : null;
  }

  public static List<Long> decodeIds(List<String> ids) {
    return ids.stream().map(id -> decodeId(id)).collect(Collectors.toList());
  }

  public static String getStaticJsonFolder() {
    return "/static/json/";
  }
}
