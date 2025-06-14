package com.nexage.app.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditUtil {

  private static final CustomObjectMapper mapper = new CustomObjectMapper();
  private static final String DELIMITER = "/";
  private static final String IDENTIFIER_NAME = "__pid__";

  /**
   * This method accepts two POJO and returns the difference in Json format
   *
   * @param pojo1
   * @param pojo2
   */
  public static JsonNode getJsonDelta(final Object pojo1, final Object pojo2) {
    JsonNode node1 = (pojo1 == null) ? null : mapper.valueToTree(pojo1);
    JsonNode node2 = (pojo2 == null) ? null : mapper.valueToTree(pojo2);

    // fill maps up with the path to each leaf - this is the key
    Map<String, String> map1 = new LinkedHashMap<>();
    if (node1 != null) fillMapFromJson(map1, node1);
    Map<String, String> map2 = new LinkedHashMap<>();
    if (node2 != null) fillMapFromJson(map2, node2);

    return getJsonDelta(map1, map2);
  }

  /**
   * Accepts two maps that represent the json structure and performs a difference between the two.
   *
   * <p>Takes in a map as follows:
   *
   * <p>Key Value --- ----- /lineitems/0/id [null,1] /lineitems/0/targetgroups/1/id [tg1,tgnew]
   *
   * <p>And creates a Json tree out of it
   *
   * <p>{ lineitems : [ { id: [null,1], targetgroups : [ { }, { id: [tg1,tgnew] } ] } ] }
   *
   * @param map1
   * @param map2
   * @return
   */
  private static JsonNode getJsonDelta(
      final Map<String, String> map1, final Map<String, String> map2) {
    Map<String, String[]> diffMap = new LinkedHashMap<>();
    fillMapWithDifferences(diffMap, map1, map2, false);
    fillMapWithDifferences(diffMap, map2, map1, true);

    JsonNode tree = mapper.createObjectNode();

    for (Entry<String, String[]> entry : diffMap.entrySet()) {
      JsonNode current = tree;

      String[] steps = entry.getKey().split("/");
      for (int i = 1; i < steps.length; i++) {
        String step = steps[i];
        String nextStep = (i < (steps.length - 1)) ? steps[i + 1] : null;

        if (nextStep == null) { // leaf node
          ArrayNode array = mapper.createArrayNode();
          log.debug("entry={}, entry.getValue()[0]={}", entry, entry.getValue()[0]);
          log.debug("entry={}, entry.getValue()[1]={}", entry, entry.getValue()[1]);
          array.insert(0, entry.getValue()[0]);
          array.insert(1, entry.getValue()[1]);
          if (current.isObject()) ((ObjectNode) current).put(step, array);
          continue;
        } else if (nextStep != null
            && isIdentifier(nextStep)) { // this node should be an array node
          JsonNode node = current.findValue(step);
          ArrayNode arrayNode = (ArrayNode) node;
          if (arrayNode == null) {
            arrayNode = mapper.createArrayNode();
            if (current.isObject()) ((ObjectNode) current).put(step, arrayNode);
          }
          current = arrayNode;
        } else if (isIdentifier(step) && current.isArray()) {
          // step is the PID of the object
          String pid = step.substring(1);
          ArrayNode arrayNode = (ArrayNode) current;

          JsonNode foundNode = null;
          for (JsonNode node : arrayNode) {
            if (node.isObject()) {
              JsonNode objectPid = node.get(IDENTIFIER_NAME);
              if (objectPid != null && pid.equals(objectPid.asText())) {
                foundNode = node;
                break;
              }
            }
          }
          if (foundNode == null) {
            foundNode = mapper.createObjectNode();
            ((ObjectNode) foundNode).put(IDENTIFIER_NAME, pid);
            arrayNode.add(foundNode);
          }
          current = foundNode;
        } else {
          ((ObjectNode) current).put(step, mapper.createObjectNode());
        }
      }
    }

    // cleanup and remove any _pid values
    List<JsonNode> nodes = tree.findParents(IDENTIFIER_NAME);
    for (JsonNode node : nodes) {
      if (node.isObject()) {
        ((ObjectNode) node).remove(IDENTIFIER_NAME);
      }
    }

    // remove any nodes that only have pid & name and the values have not changed
    cleanupPidAndNames(tree);

    return tree;
  }

  /**
   * Removes any nodes that contain only a pid and/or name and the values have not changed
   *
   * @param tnode
   */
  private static void cleanupPidAndNames(JsonNode tnode) {
    Iterator<JsonNode> iter = tnode.iterator();
    while (iter.hasNext()) {
      JsonNode node = iter.next();

      cleanupPidAndNames(node);

      if (node.has("pid") || node.has("name")) {
        int size = node.size();
        size = node.has("pid") ? size - 1 : size;
        size = node.has("name") ? size - 1 : size;
        if (size == 0) {
          boolean changed = false;
          if (node.has("pid")) {
            JsonNode value = node.get("pid");
            changed |= value.get(0) == null ? true : !value.get(0).equals(value.get(1));
          }
          if (node.has("name")) {
            JsonNode value = node.get("name");
            changed |= value.get(0) == null ? true : !value.get(0).equals(value.get(1));
          }
          // if the pid nor name have changed, remove the node
          if (!changed) {
            iter.remove();
          }
        }
      }

      // remove empty nodes
      if (node != null && node.isContainerNode() && node.size() == 0) {
        iter.remove();
      }
    }
  }

  /**
   * Compare two maps, finding the differences - this is a one way comparison
   *
   * @param differenceMap
   * @param map1
   * @param map2
   */
  private static void fillMapWithDifferences(
      final Map<String, String[]> differenceMap,
      final Map<String, String> map1,
      final Map<String, String> map2,
      boolean invert) {
    for (Entry<String, String> entry : map2.entrySet()) {
      String obj = map1.get(entry.getKey());
      if (obj == null) {
        // missing from left
        if (!invert) {
          log.debug("Store difference: {} from: {} to: {}", entry.getKey(), obj, entry.getValue());
          differenceMap.put(entry.getKey(), new String[] {null, entry.getValue()});
        } else {
          log.debug("Store difference: {} from: {} to: {}", entry.getKey(), entry.getValue(), obj);
          differenceMap.put(entry.getKey(), new String[] {entry.getValue(), null});
        }
      } else if (!obj.endsWith(entry.getValue())) {
        if (!differenceMap.containsKey(entry.getKey())) {
          // difference
          if (!invert) {
            log.debug(
                "Store difference: {} from: {} to: {}", entry.getKey(), obj, entry.getValue());
            differenceMap.put(entry.getKey(), new String[] {obj, entry.getValue()});
          } else {
            log.debug(
                "Store difference: {} from: {} to: {}", entry.getKey(), entry.getValue(), obj);
            differenceMap.put(entry.getKey(), new String[] {entry.getValue(), obj});
          }
        }
      } else if (obj.endsWith(entry.getValue())
          && (entry.getKey().endsWith("name") || entry.getKey().endsWith("pid"))) {
        if (!differenceMap.containsKey(entry.getKey())) {
          // difference
          if (!invert) {
            log.debug(
                "Store difference: {} from: {} to: {}", entry.getKey(), obj, entry.getValue());
            differenceMap.put(entry.getKey(), new String[] {obj, entry.getValue()});
          } else {
            log.debug(
                "Store difference: {} from: {} to: {}", entry.getKey(), entry.getValue(), obj);
            differenceMap.put(entry.getKey(), new String[] {entry.getValue(), obj});
          }
        }
      }
    }
  }

  /**
   * Creates a map from a json object where the key is the json path
   *
   * <p>For example: { lineitems : [ { id: 1, targetgroups : [ { id: tg1 }, { id: tg2 } ] } ] }
   *
   * <p>Key Value --- ----- /lineitems/0/id 1 /lineitems/0/targetgroups/0/id tg1
   * /lineitems/0/targetgroups/1/id tg2
   *
   * @param map
   * @param node
   */
  private static void fillMapFromJson(Map<String, String> map, JsonNode node) {
    fillMapFromJson(map, node, "", "");
  }

  /**
   * @param map
   * @param node
   * @param path
   * @param name
   */
  private static void fillMapFromJson(
      Map<String, String> map, JsonNode node, String path, String name) {

    if (node.isArray()) {
      ArrayNode array = (ArrayNode) node;
      int i = 0;
      for (JsonNode entry : array) {
        Object pid = null;
        if (entry.isObject()) {
          pid = ((ObjectNode) entry).get("pid");
        }
        fillMapFromJson(
            map,
            entry,
            new StringBuilder()
                .append(path)
                .append(name)
                .append(DELIMITER)
                .append("@")
                .append(pid != null ? pid : i++)
                .toString(),
            "");
      }
    } else if (node.isObject()) {
      ObjectNode object = (ObjectNode) node;
      Iterator<Entry<String, JsonNode>> iter = object.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> entry = iter.next();
        fillMapFromJson(
            map,
            entry.getValue(),
            new StringBuilder().append(path).append(name).append(DELIMITER).toString(),
            entry.getKey());
      }
    } else {
      log.debug(" Add key: {} value: {}", path + name, ((ValueNode) node).asText());
      map.put(path + name, ((ValueNode) node).asText());
    }
  }

  private static boolean isIdentifier(String s) {
    return s.startsWith("@") && s.substring(1).matches("[-+]?\\d*\\.?\\d+");
  }
}
