package com.ibiscus.shopnchek.domain.debt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="branchs")
public class Branch {

	@Id
	@Column(name = "id", nullable = false)
	private long id;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getAddress() {
		return address;
	}
}
