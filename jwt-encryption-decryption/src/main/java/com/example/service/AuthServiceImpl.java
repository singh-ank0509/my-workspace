package com.example.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.example.config.JwtUtil;
import com.example.dto.LoginRequestDto;

import javaldapapp.AssignOfficeDTO;
import javaldapapp.JavaLDapConnection;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private Set<String> blacklist = new HashSet<>();
	
	@Autowired
    private JwtUtil jwtUtil;
	
	@Override
	public ResponseEntity<?> authenticateAndGenerateToken(LoginRequestDto loginRequestDto, HttpServletRequest request) {
		
		String username = loginRequestDto.getUsername();
		AssignOfficeDTO assignOfficeDTO = new AssignOfficeDTO();
		try {
			JavaLDapConnection javaLDapConnection = new JavaLDapConnection();
			
			if(username.length() == 7) {
				username = "0"+username;
			}
			
			assignOfficeDTO = (AssignOfficeDTO) javaLDapConnection.authenticate(username, loginRequestDto.getPassword());			
			if(assignOfficeDTO == null) {
				throw new BadCredentialsException("Unauthorized : Access Denied - Wrong Credentials -> Invalid Username or Password!!");
			}
		} catch(NullPointerException npe) {
			logger.info("User {} does not exist in LDAP directory!!", username);
			return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body(String.format("Access Denied: User %s does not exist in LDAP directory.", username));
		}
		
		String jwtToken = this.jwtUtil.generateJwtToken(loginRequestDto.getUsername());
		return ResponseEntity.ok(jwtToken);
	}
	
	@Override
	public boolean isBlacklisted(String token) {
		return blacklist.contains(token);
	}
}
