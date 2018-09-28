package com.ibiscus.shopnchek.domain.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="users")
public class User implements UserDetails {

	private static final long serialVersionUID = 6755708810467824702L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="username", nullable=false, length=100)
	private String username;

	@Column(name="name", nullable=false, length=255)
	private String name;

	@Column(name="password", nullable=false, length=100)
	private String password;

	@Column(name="enabled")
	private boolean enabled;

	@Column(name="email", length=100)
	private String email;

	@ManyToMany
	@JoinTable(name="users_roles",
			joinColumns=@JoinColumn(name="user_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))  
	private Set<Role> roles = new HashSet<Role>();

	User() {
	}

	public User(final String username, final String name, final String password, final boolean enabled, String email,
				final Set<Role> roles) {
		this.username = username;
		this.name = name;
		this.password = password;
		this.enabled = enabled;
		this.email = email;
		this.roles.addAll(roles);
	}

	public void update(final String username, final String name, final String password,
			final boolean enabled, String email, final Set<Role> roles) {
		this.username = username;
		this.name = name;
		this.password = password;
		this.enabled = enabled;
		this.email = email;
		this.roles.clear();
		this.roles.addAll(roles);
	}

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public boolean hasRole(final Role role) {
		boolean roleFound = false;
		Iterator<Role> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext() && !roleFound) {
			Role currentRole = rolesIterator.next();
			roleFound = currentRole.getId() == role.getId();
		}
		return roleFound;
	}

	public boolean isAdministrator() {
		boolean hasAdminRole = false;
		Iterator<Role> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext() && !hasAdminRole) {
			hasAdminRole = rolesIterator.next().getName().equals(Role.ADMINISTRATOR_ROLE_NAME);
		}
		return hasAdminRole;
	}

	public boolean hasFeature(final String featureName) {
		boolean featureFound = isAdministrator();
		Iterator<Role> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext() && !featureFound) {
			Role role = rolesIterator.next();
			Iterator<Feature> featuresIterator = role.getFeatures().iterator();
			while (featuresIterator.hasNext() && !featureFound) {
				featureFound = featuresIterator.next().getName().equals(featureName);
			}
		}
		return featureFound;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Role role : roles) {
			for (Feature feature : role.getFeatures()) {
				authorities.add(new SimpleGrantedAuthority(feature.getName()));
			}
		}
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
