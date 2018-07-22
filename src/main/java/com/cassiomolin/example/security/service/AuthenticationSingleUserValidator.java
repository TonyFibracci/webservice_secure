package com.cassiomolin.example.security.service;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;

import com.cassiomolin.example.security.exception.MultipleUserException;

@ApplicationScoped
public class AuthenticationSingleUserValidator {
	
	private static HashMap<String,String> userTokenMapping;
	
	public void addUserTokenMapping(String user, String token) {
		if(userTokenMapping == null) {
			userTokenMapping = new HashMap<>();
		}	
		userTokenMapping.put(user, token);
	}
	
	public void isUserTokenValid(String user, String token) {
		if(userTokenMapping.containsKey(user)) {
			if(!userTokenMapping.get(user).equals(token))
				throw new MultipleUserException("Multiple users");
		}
		else
			throw new MultipleUserException("Multiple users");
	}

}
