package com.ibiscus.shopnchek.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="features")
public class Feature {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="name", nullable=false, length=100)
	private String name;

	Feature() {
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
