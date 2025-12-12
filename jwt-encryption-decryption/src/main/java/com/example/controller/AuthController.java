package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.LoginRequestDto;
import com.example.service.AuthService;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticateAndGenerateToken(@RequestBody LoginRequestDto loginRequestDto,
														HttpServletRequest request) {
		String ipAddress = request.getRemoteHost();
		System.err.println("ipAddress - " + ipAddress);
		String username = loginRequestDto.getUsername();
		String pwd = loginRequestDto.getPassword();
		if(username == null || username.isBlank() || pwd == null || pwd.isBlank()) {
			return new ResponseEntity<>("Validation Error -> Required field is not provided", HttpStatus.BAD_REQUEST);	
		}
		ResponseEntity<?> token = authService.authenticateAndGenerateToken(loginRequestDto, request);
		return ResponseEntity.ok(Map.of("token", token.getBody()));
	}
	
	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome, Validation Succes!!";
	}
}
