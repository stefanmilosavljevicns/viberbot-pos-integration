package com.payten.FoodRest.service.impl;

import java.util.List;

import com.payten.FoodRest.dto.UserRequest;
import com.payten.FoodRest.model.User;
import com.payten.FoodRest.repository.UserRepository;
import com.payten.FoodRest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	public User findById(Long id) throws AccessDeniedException {
		return userRepository.findById(id).orElseGet(null);
	}

	public List<User> findAll() throws AccessDeniedException {
		return userRepository.findAll();
	}

	@Override
	public User save(UserRequest userRequest) {
		User u = new User();
		u.setUsername(userRequest.getUsername());
		
		// pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
		// treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
		u.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		
		u.setFirstName(userRequest.getFirstname());
		u.setLastName(userRequest.getLastname());
		u.setEnabled(true);
		u.setEmail(userRequest.getEmail());

//		// u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
//		List<Role> roles = roleService.findByName("ROLE_USER");
//		u.setRoles(roles);

		return this.userRepository.save(u);
	}

}
