package com.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth.config.JWTTokenUtil;
import com.auth.model.JWTRequest;
import com.auth.model.JWTResponse;

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JWTTokenUtil jwtTokenUtil;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JWTRequest jwtRequest) throws Exception{
		Object details = authenticate(jwtRequest.getUsername(), jwtRequest.getPassword()).getDetails();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
		
		String jwtToken = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new JWTResponse(jwtToken));
		
	}
	private Authentication authenticate(String username, String password)throws Exception {
		try {
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}