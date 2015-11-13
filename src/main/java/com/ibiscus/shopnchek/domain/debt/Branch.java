package com.ibiscus.shopnchek.domain.debt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="branchs")
public class Branch {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "city", length = 255)
	private String city;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	Branch() {
	}

	public Branch(final Client client, final String code, final String address) {
		this.client = client;
		this.code = code;
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public Client getClient() {
		return client;
	}
}
