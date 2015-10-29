package com.ibiscus.shopnchek.application.security;

import java.util.List;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;

public class SearchUserCommand extends SearchCommand<User> {

	private UserRepository userRepository;

	private String name;

	SearchUserCommand() {
	}

	@Override
	protected List<User> getItems() {
		return userRepository.find(getStart(), getPageSize(), getOrderBy(), isAscending(),
				name);
	}

	@Override
	protected int getCount() {
		return userRepository.getCount(name);
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
