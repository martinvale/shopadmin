package com.ibiscus.shopnchek.domain.security;

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

@Entity
@Table(name = "roles")
public class Role {

	static final String ADMINISTRATOR_ROLE_NAME = "Administrador";

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="name", nullable=false, length=100)
	private String name;

	@ManyToMany
	@JoinTable(name="roles_features",
			joinColumns=@JoinColumn(name="role_id"),
			inverseJoinColumns=@JoinColumn(name="feature_id"))  
	private Set<Feature> features;

	Role() {
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Feature> getFeatures() {
		return features;
	}
}
