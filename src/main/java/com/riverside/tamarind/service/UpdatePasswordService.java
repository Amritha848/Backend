package com.riverside.tamarind.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.riverside.tamarind.entity.UpdatePasswordEntity;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.repository.UserRepository;

@Service
public class UpdatePasswordService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public User updatePassword(UpdatePasswordEntity updatePasswordEntity, Principal principal) {
		
		User users=null;
		
		System.out.println(principal.getName());
		
		var user = ((User)((UsernamePasswordAuthenticationToken) principal).getPrincipal());
		
		if(user instanceof User) {
			
			users = ((User)((UsernamePasswordAuthenticationToken) principal).getPrincipal());
			users.setPassword(passwordEncoder.encode(updatePasswordEntity.getNewPassword()));
			userRepository.save(users);
		}else {
			
			return null;
		}
		
		if (!passwordEncoder.matches(updatePasswordEntity.getOldPassword(), user.getPassword())) {
			throw new RuntimeException("Wrong password");
		}
		if (!updatePasswordEntity.getNewPassword().equals(updatePasswordEntity.getConfirmPaSsword())) {
			throw new RuntimeException("passwords are not same");
		}

		
		return users;

	}

}
