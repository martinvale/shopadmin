package com.ibiscus.shopnchek.application.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.security.Role;
import com.ibiscus.shopnchek.domain.security.RoleRepository;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;

public class SaveUserCommand implements Command<User> {

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private Long userId;

	private String username;

	private String name;

	private String password;

	private boolean enabled;

	private Set<Long> roleIds;

	SaveUserCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public User execute() {
		Set<Role> roles = new HashSet<Role>();
		for (Long roleId : roleIds) {
			roles.add(roleRepository.get(roleId));
		}
		User user;
		if (userId == null) {
			user = new User(username, name, password, enabled, roles);
			userRepository.save(user);
		} else {
			user = userRepository.get(userId);
			user.update(username, name, password, enabled, roles);
		}
		return user;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setRoleIds(Set<Long> roleIds) {
		this.roleIds = roleIds;
	}

}
