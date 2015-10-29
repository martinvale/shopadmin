package com.ibiscus.shopnchek.application.security;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;

public class GetUserCommand implements Command<User> {

	private UserRepository userRepository;

	private long userId;

	@Override
	public User execute() {
		return userRepository.get(userId);
	}

	public void setUserRepository(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}
}
