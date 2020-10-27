package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.UsernameAlreadyExistsException;
import com.example.demo.insert.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public User saveUser(User newUser) {
		
		try {
			newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
			
			//Username has to be unique(exception)
			newUser.setUsername(newUser.getUsername());
			
			// make sure that password and confirm password match
			newUser.setConfirmPassword("");
			return userRepository.save(newUser);
		}catch(Exception e) {
			throw new UsernameAlreadyExistsException("Username "+newUser.getUsername()+ " already exists");
		}
	}

}
