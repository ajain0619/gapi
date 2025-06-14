package com.nexage.geneva.util.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by seanryan on 01/03/2016.
 *
 * <p>This class calls a string in format of json array and converts it to a JSONArray object.
 *
 * <p>This class can be initialized be using sample code as follows:
 *
 * <p>// JSON file sample JsonTxtToArray jsonTxtToArray = new
 * JsonTxtToArray("/Users/<Username>/Documents/<filename>.txt"); JSONArray supplierArray =
 * jsonTxtToArray.getJsonTxtToArray();
 */
public class JsonTxtToArray {

  private static Logger logger = LoggerFactory.getLogger(JsonTxtToArray.class);
  private String filePath = null;
  JSONObject jsonObject;

  public JsonTxtToArray(String filePath) {
    this.filePath = filePath;
  }

  public JSONArray getJsonTxtToArray() {
    String jsonData = "";
    BufferedReader br = null;
    JSONArray jsonArray = null;

    try {
      String line;
      br = new BufferedReader(new FileReader(filePath));
      while ((line = br.readLine()) != null) {
        jsonData += line + "\n";
      }

      jsonArray = new JSONArray(jsonData);
      int count = jsonArray.length(); // get totalCount of all jsonObjects
      for (int i = 0; i < count; i++) { // iterate through jsonArray
        jsonObject = jsonArray.getJSONObject(i); // get jsonObject @ i position
        logger.debug("jsonObject " + i + ": " + jsonObject);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null) br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return jsonArray;
  }
}
