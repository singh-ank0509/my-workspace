package com.example.testt;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
	
	private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

	@Override
	public String test(String data) {
		logger.info("Test API called... " + LocalDateTime.now());
		logger.info("Data : " + data);
		return data;
	}
}
