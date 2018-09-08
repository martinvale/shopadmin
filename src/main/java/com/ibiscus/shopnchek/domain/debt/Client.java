package com.ibiscus.shopnchek.domain.debt;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="clients")
public class Client {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "country", nullable = true, length = 255)
	private String country;

	@OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private Set<Branch> branchs = new HashSet<Branch>();

	Client() {
	}

	public Client(final String name) {
		this.name = name;
	}

	public Client(final String name, String country) {
		this.name = name;
		this.country = country;
	}

	public void addBranch(final Branch branch) {
		branchs.add(branch);
	}

	public void update(String name, String country) {
		this.name = name;
		this.country = country;
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

	public String getCountry() {
		return country;
	}
}
