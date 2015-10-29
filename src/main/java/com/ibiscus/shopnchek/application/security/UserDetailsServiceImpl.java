package com.ibiscus.shopnchek.application.security;

import org.apache.commons.lang.Validate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(final UserRepository theUserRepository) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    userRepository = theUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("The user does not exists");
    }
    return user;
  }

}
