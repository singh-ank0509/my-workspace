package com.spring.exception.service;


import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JsonParserService {

	public List<Map<String, String>> parseJson(String json) throws ParseException {
	    JSONParser parser = new JSONParser();
	    JSONObject jsonObject = (JSONObject) parser.parse(json);
	    Object obj = jsonObject.get("DATA");
	    JSONArray dataArray = null; // declare dataArray here

	    if (obj instanceof JSONArray) {
	        dataArray = (JSONArray) obj;
	    } else {
	        throw new RuntimeException("");
	    }

	    List<Map<String, String>> parsedData = new ArrayList<>();

	    for (int i = 0; i < dataArray.size(); i++) { // use size() instead of length()
	        JSONObject dataObject = (JSONObject) dataArray.get(i); // use get() instead of getJSONObject()
	        String rawData = (String) dataObject.get("raw_data"); // use get() instead of getString()
	        String[] rawDataArray = rawData.split(",");

	        Map<String, String> dataMap = new HashMap<>();
	        for (int j = 0; j < rawDataArray.length; j++) {
	            dataMap.put("key_" + j, rawDataArray[j]);
	        }
	        parsedData.add(dataMap);
	    }

	    return parsedData;
	}
}
