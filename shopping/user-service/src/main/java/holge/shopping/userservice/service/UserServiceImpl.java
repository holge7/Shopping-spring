package holge.shopping.userservice.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import commons.dto.UserDTO;
import commons.enums.ERol;
import holge.shopping.userservice.entity.Rol;
import holge.shopping.userservice.entity.User;
import holge.shopping.userservice.exception.RolNotFoundException;
import holge.shopping.userservice.exception.UserAlreadyExistsException;
import holge.shopping.userservice.exception.UserNotFoundException;
import holge.shopping.userservice.mapper.UserMapper;
import holge.shopping.userservice.repository.RolRepository;
import holge.shopping.userservice.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	private UserRepository userRepository;
	private RolRepository rolRepository;
	private PasswordEncoder passwordEncoder;
	private UserMapper userMapper;
	
	public UserServiceImpl(UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder,
			UserMapper userMapper) {
		this.userRepository = userRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
	}

	@Override
	public UserDTO login(String email, String password){
		
		// Validate the existence of the user/password
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException(email));

		// Check if match encode passwords
		if (passwordEncoder.matches(password, user.getPassword())) {
			return userMapper.toDto(user);			
		}
		
		// Throw user not foud to dont share if the error is from the email or password
		throw new UserNotFoundException(email);
		
	}
	
	@Override
	public boolean delete(Long id) {
		
		boolean exists = userRepository.existsById(id);
		
		if (!exists) {
			throw new UserNotFoundException(id.toString());
		}
		
		userRepository.deleteById(id);
		return true;
	}
	
	@Override
	public UserDTO register(String name, String email, Set<ERol> rol, String password) {

		//Check that not exists user already with that email
		if (userRepository.existsByEmail(email)) {
			String response = String.format("User with email [%s] already exist", email);
			throw new UserAlreadyExistsException(response);
		}
		
		//Create user
		User user = new User(
					email,
					name,
					passwordEncoder.encode(password)
				);
		
		//Assign roles
		Set<Rol> rolesChecked = new HashSet<>();
		
		if (rol == null) {
			Rol userRole = rolRepository.findByRol(ERol.ROLE_USER)
					.orElseThrow(() -> new RolNotFoundException(ERol.ROLE_USER.toString()));
			
					rolesChecked.add(userRole);
		} else {

			for (ERol role : rol) {
				// Get rol and assert that exists, then add to list
				Optional<Rol> userRole = rolRepository.findByRol(role);

				if (!userRole.isPresent()) {
					throw new RolNotFoundException(role.toString());
				}

				rolesChecked.add(userRole.get());
			}
		}
		
		user.setRol(rolesChecked);

		//Save and transform user
		return userMapper.toDto(userRepository.save(user));
	}
	
}
