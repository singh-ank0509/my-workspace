package com.example.service;

import org.springframework.http.ResponseEntity;

import com.example.dto.LoginRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

	ResponseEntity<?> authenticateAndGenerateToken(LoginRequestDto loginRequestDto, HttpServletRequest request);

	boolean isBlacklisted(String token);
}
