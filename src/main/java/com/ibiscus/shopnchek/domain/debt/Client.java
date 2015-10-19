package com.ibiscus.shopnchek.domain.debt;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="clients")
public class Client {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@OneToMany
	@JoinColumn(name = "client_id")
	private Set<Branch> branchs;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Branch> getBranchs() {
		return branchs;
	}
}
