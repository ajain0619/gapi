package com.nexage.app.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class AuditUtilTest {

  @Test
  void testGetJsonDelta() {
    Class1 objectA = new Class1();
    objectA.pid = 4L;
    objectA.name = "Before";

    Class2 childA1 = new Class2();
    childA1.pid = 8L;
    childA1.name = "Child1";

    objectA.children.add(childA1);

    Class1 objectB = new Class1();
    objectB.pid = 4L;
    objectB.name = "After";

    Class2 childB1 = new Class2();
    childB1.pid = 4L;
    childB1.name = "Child2";

    Class2 childB2 = new Class2();
    childB2.pid = 8L;
    childB2.name = "Child1";

    objectB.children.add(childB2);
    objectB.children.add(childB1);

    JsonNode response = AuditUtil.getJsonDelta(objectB, objectA);
    log.debug(response.toString());
  }

  @Test
  void testGetJsonDeltaMap() {
    Map<String, String> map1 = new HashMap<>();
    map1.put("lineitem/@13/pid", "13");
    // map1.put("lineitem/@13/price", "0.11");
    // map1.put("lineitem/@13/status", "INACTIVE");
    map1.put("lineitem/@13/targetGroups/@40/pid", "40");
    map1.put("lineitem/@13/targetGroups/@40/name", "TG40");
    map1.put("lineitem/@13/targetGroups/@67/pid", "67");
    map1.put("lineitem/@24/pid", "24");
    map1.put("lineitem/@24/name", "LI24");
    map1.put("lineitem/@24/creatives", null);
    map1.put("lineitem/@57/pid", "57");
    map1.put("lineitem/@57/name", "LI57");

    Map<String, String> map2 = new HashMap<>();
    map2.put("lineitem/@13/pid", "13");
    // map2.put("lineitem/@13/status", "ACTIVE");
    map2.put("lineitem/@13/targetGroups/@40/pid", "40");
    map2.put("lineitem/@13/targetGroups/@40/name", "TG40");
    map2.put("lineitem/@13/targetGroups/@67/pid", "67");
    map2.put("lineitem/@13/targetGroups/@40/targets/@1/pid", "1");
    map2.put("lineitem/@13/targetGroups/@40/targets/@1/targetType", "IABCATEGORY");
    map2.put("lineitem/@24/pid", "24");
    map2.put("lineitem/@24/name", "LI24");
    map2.put("lineitem/@24/creatives", null);
    map2.put("lineitem/@35/pid", "35");

    JsonNode response = AuditUtil.getJsonDelta(map2, map1);
    log.debug(response.toString());
  }

  public static class Class1 {
    public Long pid;
    public String name;
    public Set<Class2> children = new HashSet<>();

    public Long getPid() {
      return pid;
    }

    public void setPid(Long pid) {
      this.pid = pid;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Set<Class2> getChildren() {
      return children;
    }

    public void setChildren(Set<Class2> children) {
      this.children = children;
    }
  }

  public static class Class2 {

    public Long pid;
    public String name;

    public Long getPid() {
      return pid;
    }

    public void setPid(Long pid) {
      this.pid = pid;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
