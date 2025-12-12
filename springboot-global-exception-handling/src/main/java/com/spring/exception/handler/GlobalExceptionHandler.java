package com.spring.exception.handler;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementsException(NoSuchElementException exception) {
		return new ResponseEntity<String>("Employee Not Found!!", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({ResourceNotFoundException.class})
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exception) {
		exception.printStackTrace();
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}
}
