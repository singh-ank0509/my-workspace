package com.example.testt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	private TestService testService;
	
	@PostMapping("/test/{data}")
	public ResponseEntity<String> getReportSearch(@PathVariable String data) {
		return ResponseEntity.ok(testService.test(data));
	}
}
