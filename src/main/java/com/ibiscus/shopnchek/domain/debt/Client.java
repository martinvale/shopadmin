package com.ibiscus.shopnchek.domain.debt;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	@OneToMany(mappedBy = "client")
	private Set<Branch> branchs = new HashSet<Branch>();

	Client() {
	}

	public Client(final String name) {
		this.name = name;
	}

	public void addBranch(final Branch branch) {
		branchs.add(branch);
	}

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
