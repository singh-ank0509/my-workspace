package com.spring.exception.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.exception.service.JsonParserService;

import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

@RestController
public class ResponseController {
	
	@Autowired
    private JsonParserService jsonParserService;

    @GetMapping("/parse-json")
    public List<Map<String, String>> parseJson() throws ParseException {
		String json = """
				{
				    "API_TIME_MS": 5156,
				    "STATUS": "SUCCESS",
				    "DATA": [
				        {
				            "_id": "40a4cf72-79ad-11ee-860f-0a47c63b9fbc:2024-08-21 23:30:00.0",
				            "raw_data": "2024-08-21 23:30:00.0,0.15,0.61,0.15,219.5,222.0,220.20000000000002,50.0,70.0,0.0,0.0,0.0,0.0,0.0,50.0,100.0,140.0,0.0,100.0,0.71,0.5700000000000001,41.0,1.0,1.0,1.0,0.0,0.0,0.0"
				        },
				        {
				            "_id": "40a4cf72-79ad-11ee-860f-0a47c63b9fbc:2024-08-21 23:00:00.0",
				            "raw_data": "2024-08-21 23:00:00.0,0.26,1.19,0.15,218.4,221.3,218.9,130.0,130.0,0.0,0.0,20.0,0.0,0.0,0.0,260.0,260.0,40.0,0.0,1.0,1.21,41.0,0.0,0.0,1.0,0.0,32.0,0.0"
				        },
				        {
				            "_id": "40a4cf72-79ad-11ee-860f-0a47c63b9fbc:2024-08-21 22:30:00.0",
				            "raw_data": "2024-08-21 22:30:00.0,0.15,0.79,0.15,218.0,219.9,217.60000000000002,70.0,80.0,0.0,0.0,10.0,0.0,0.0,40.0,140.0,160.0,20.0,80.0,0.87,0.78,41.0,0.0,0.0,0.0,0.0,9.0,0.0"
				        }
				    ],
				    "STRUCTURE": "0.0.1.0.0.255,1.0.31.27.0.255,1.0.51.27.0.255,1.0.71.27.0.255,1.0.32.27.0.255,1.0.52.27.0.255,1.0.72.27.0.255,1.0.1.29.0.255,1.0.9.29.0.255,1.0.2.29.0.255,1.0.10.29.0.255,1.0.5.29.0.255,1.0.6.29.0.255,1.0.7.29.0.255,1.0.8.29.0.255,1.0.1.27.0.255,1.0.9.27.0.255,1.0.5.27.0.255,1.0.8.27.0.255,1.0.13.27.0.255,1.0.91.27.0.255,1.0.128.8.98.255,1.0.128.7.17.255,1.0.128.7.18.255,1.0.128.7.19.255,1.0.128.7.21.255,1.0.128.7.22.255,1.0.128.7.23.255"
				}                """;
        return jsonParserService.parseJson(json);
    }
}
